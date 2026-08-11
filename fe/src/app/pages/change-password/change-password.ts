import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../services/auth-service';
import { getUser } from '../../util/session-storage';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { UserService } from '../../services/user-service/user-service';
import { LoadingComponent } from "../../components/loading-component/loading-component";
import { Router } from '@angular/router';

@Component({
  selector: 'app-change-password',
  imports: [NavbarComponent, TranslateModule, ReactiveFormsModule, LoadingComponent],
  templateUrl: './change-password.html',
  styleUrl: './change-password.css',
})
export class ChangePassword {

  isOldPasswordCorrect: boolean = true;
  isPasswordValid: boolean = true;
  isPasswordMatch: boolean = true;

  isSendingRequest:boolean = false;

  constructor(
    @Inject(PLATFORM_ID) private platformId:Object,
    private authService:AuthService,
    private userService: UserService,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ){}

  passwordForm = new FormGroup({
    oldPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
    newPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
  });

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.passwordForm.get("oldPassword")?.valueChanges.subscribe(()=>{
        this.isOldPasswordCorrect = true;
      })

      this.passwordForm.get("newPassword")?.valueChanges.subscribe(()=>{
        this.isPasswordValid = this.passwordForm.get('newPassword')?.valid ?? false;
      })

      this.passwordForm.get("confirmPassword")?.valueChanges.subscribe(()=>{
        this.isPasswordMatch = true
      })
    }
  }

  onSubmit(){
    const {oldPassword, newPassword, confirmPassword} = this.passwordForm.value
    const user = getUser()
    if(!user){
      const message = this.translate.instant("error.An-error-has-occurred,-please-login-again");
      alert(message);
      return;
    }
    if(oldPassword && newPassword && confirmPassword){
      if(newPassword !== confirmPassword){
        this.isPasswordMatch = false;
        return
      }
      if(this.isPasswordValid == false){
        return
      }
      if(oldPassword == newPassword){
        const message = this.translate.instant("error.Please-enter-a-different-password");
        alert(message);
        return
      }
      this.isSendingRequest = true;
      this.authService.verifyPassword(user?.username, oldPassword).subscribe({
        next:(data:any)=>{
          if(data.data == true){
            this.isOldPasswordCorrect = true;
            user.password = newPassword;
            this.userService.updateUser(user).subscribe({
              next: (data:any)=>{
                if(data.code == "200"){
                  const message = this.translate.instant("usersManagement.Password-is-changed")
                  alert(message)
                  this.clearForm();
                }
                this.isSendingRequest = false;
                this.cdr.markForCheck();
                return;
              },
              error:(err:HttpErrorResponse)=>{
                errorNoti(err, this.translate)
                this.isSendingRequest = false;
                this.cdr.markForCheck();
                return
              }
            })
          }
          else{
            this.isOldPasswordCorrect = false;
            this.isSendingRequest = false;
            this.cdr.markForCheck()
            return;
          }
        },
        error: (err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
          this.isSendingRequest = false;
                this.cdr.markForCheck();
          return;
        }
      })
    }
  }

  clearForm(){
    this.passwordForm.patchValue({
      oldPassword: "",
      newPassword: "",
      confirmPassword: ""
    })
    this.isOldPasswordCorrect = true;
    this.isPasswordMatch = true;
    this.isPasswordValid = true;
    this.cdr.markForCheck();
  }

  handleCancel(){
    this.router.navigate(['/settings'])
  }
}
