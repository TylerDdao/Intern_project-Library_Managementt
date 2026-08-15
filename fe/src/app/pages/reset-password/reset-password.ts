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
import { ActivatedRoute, Router } from '@angular/router';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { LanguageSelector } from "../../components/language-selector/language-selector";

@Component({
  selector: 'app-reset-password',
  imports: [TranslateModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {

  isCodeCorrect: boolean | null = null;
  
  isPasswordValid: boolean = true;
  isPasswordMatch: boolean = true;

  constructor(
    @Inject(PLATFORM_ID) private platformId:Object,
    private authService:AuthService,
    private userService: UserService,
    private translate: TranslateService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private router: Router
  ){}

  passwordForm = new FormGroup({
    newPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
  });

  handleBackToLogin(){
    this.router.navigate(['/login'])
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.passwordForm.get("newPassword")?.valueChanges.subscribe(()=>{
        this.isPasswordValid = this.passwordForm.get('newPassword')?.valid ?? false;
      })

      this.passwordForm.get("confirmPassword")?.valueChanges.subscribe(()=>{
        this.isPasswordMatch = true
      })

      const code = Number(this.route.snapshot.paramMap.get("code"))
      const email = String(this.route.snapshot.paramMap.get("email"))
      this.authService.verifyResetPasswordCode(code, email).subscribe({
        next:(data:any)=>{
          if(data.data == true){
            this.isCodeCorrect = true;
          }
          else{
            this.router.navigate(["/unauthorized"])
          }
          this.cdr.markForCheck()
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
          this.cdr.markForCheck()
        }
      })
    }
  }

  onSubmit(){
    const {newPassword, confirmPassword} = this.passwordForm.value
    const email = String(this.route.snapshot.paramMap.get("email"))
    if(newPassword && confirmPassword && email){
      if(newPassword !== confirmPassword){
        this.isPasswordMatch = false;
        return
      }
      if(this.isPasswordValid == false){
        return
      }
      this.authService.resetPassword(email, newPassword).subscribe({
        next:(data:any)=>{
          if(data.data == true){
            const message = this.translate.instant("forgotPassword.Your-password-is-reset")
            alert(message)
            this.router.navigate(['/login'])
          }
          else{
            const message = this.translate.instant("error.An-error-has-occurred")
            alert(message)
          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
        }
      })
    }
  }

  clearForm(){
    this.passwordForm.patchValue({
      newPassword: "",
      confirmPassword: ""
    })
    this.isPasswordMatch = true;
    this.isPasswordValid = true;
    this.cdr.markForCheck();
  }
}
