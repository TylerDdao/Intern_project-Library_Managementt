import { ChangeDetectorRef, Component, Inject, NgZone, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Route, Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { LanguageService } from '../../services/language-service/language-service';
import { User } from '../../models/user';
import { Role } from '../../models/role';
import { HttpErrorResponse } from '@angular/common/http';
import { NewUserForm } from '../../forms/new-user-form/new-user-form';
import { UserService } from '../../services/user-service/user-service';
import { isPlatformBrowser } from '@angular/common';
import { errorNoti } from '../../util/error-notification';
import { LoadingComponent } from "../../components/loading-component/loading-component";
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";
import { Announcement } from '../../models/announcement';
import { webAnnouncements } from '../../../assets/constants';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';

@Component({
  selector: 'app-signup',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, LoadingComponent, AnnouncementComponent],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  invalidInformation = false;
  signupCompleted = false;
  user!:User
  isRegister:boolean = false;

  constructor(
    private authService: AuthService,
    public langService: LanguageService,
    protected router: Router,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private announcementService: AnnouncementService
  ) 
  {}

  isPasswordMatch:boolean = true;
  isPasswordValid:boolean = true;

  isUsernameInvalid: boolean = false;
  isUsernameAvailable : boolean | null = null;

  isVerifyingEmail:boolean = false;
  isEmailVerified:boolean=false;
  isEmailInvalid: boolean = false;
  isSendingVerificationEmail:boolean = false;

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(?:(?:0|\+84)\d{9}|(?:\+1)?\d{10})$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    verificationCode: new FormControl('', Validators.required),
    password: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)]),
    confirmPassword: new FormControl('', [Validators.required, Validators.pattern(/^(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/)])
  });

  announcements:Announcement[] =[]
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
    this.announcements = this.announcementService.getAnnouncements();
    this.cdr.markForCheck();
  }

  handleSendVerificationCode(){
    this.isVerifyingEmail = true;
    this.isSendingVerificationEmail = true;
    this.newUserForm.get('verificationCode')?.enable();
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    const name = this.translate.instant("user.New-user")
    console.log(this.translate.instant("user.New-user"))
    console.log(name)
    this.authService.sendVerificationCode(email, name).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("verification.Your-verification-code-has-been-sent-to-your-email");
          alert(message)
          
        }
        if(data.code == "CODE-ALREADY-SENT"){
          const message = this.translate.instant("verification.Your-verification-code-has-already-been-sent-to-your-email");
          alert(message +"\n" + data.code)
        }
        if(data.code == "EMAIL-IN-USE"){
          const message = this.translate.instant("verification.Email-has-been-used");
          alert(message +"\n" + data.code)
          this.isVerifyingEmail = false;
        }
        this.isSendingVerificationEmail = false;
        this.cdr.markForCheck();
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isSendingVerificationEmail = false;
      }
    })
    this.cdr.markForCheck();
  }

  handleVerify(){
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    const verificationCode = this.newUserForm.get("verificationCode")?.value?.trim().toLowerCase();
    if(!verificationCode){
      return;
    }
    this.authService.submitVerificationCode(email, verificationCode).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          if(data.data.verified){
            const message = this.translate.instant("verification.Your-email-has-been-verified");
            alert(message)
            this.isEmailVerified = true;
            this.isEmailInvalid = false;
            this.newUserForm.get('verificationCode')?.disable();
          }
          else{
            const message = this.translate.instant("verification.Incorrect-verification-code");
            alert(message)
          }
          this.cdr.markForCheck();
        }
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
      }
    })
  }

  onSubmit(){
    this.isRegister =true;
    const {username, fullName, phoneNumber, email, address, password, confirmPassword} = this.newUserForm.value;
    if(password!=confirmPassword){
      this.isPasswordMatch = false;
      this.isRegister = false;
      this.cdr.markForCheck();
      return;
    }
    if(this.isUsernameAvailable == false || this.isUsernameAvailable == null){
        this.isUsernameInvalid = true;
        this.isRegister = false;
        return;
      }
    if(this.isEmailVerified == false){
      this.isEmailInvalid = true;
      this.isRegister = false;
      return;
    }
    if(username && fullName && phoneNumber && email && password){
      this.user = {
        username,
        fullName,
        email,
        phoneNumber,
        password,
        address: address ?? ''
      };

      this.authService.signup(this.user).subscribe({
        next:(data:any)=>{
          if(data.code == "200"){
            this.router.navigate(['/signup/success'])
          }
          this.isRegister = false;
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
          this.isRegister = false;
        }
      })

    }
  }

  checkUsername() {
    this.isUsernameInvalid = false;
    const username = this.newUserForm.get('username')?.value?.trim();
    if (!username){
      return;
    }

    this.userService.checkUsernameAvailability(username).subscribe({
      next: (data: any) => {
        if(data.data === true){
          this.isUsernameAvailable = true;
          this.isUsernameInvalid = false;
        }
        else{
          this.isUsernameAvailable = false;
        }
        this.cdr.markForCheck();
      },
      error: (err:HttpErrorResponse) =>{
        errorNoti(err, this.translate)
      } 
    });
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
      this.announcements = this.announcementService.getAnnouncements()

      this.newUserForm.get('username')?.valueChanges.subscribe(() => {
        this.isUsernameAvailable = null;
        this.isUsernameInvalid = false;
        this.cdr.markForCheck();
      });
      this.newUserForm.get('email')?.valueChanges.subscribe(() => {
        this.isEmailInvalid = false;
        this.isEmailVerified = false;
        this.newUserForm.get('verificationCode')?.enable();
        this.cdr.markForCheck();
      });
      this.newUserForm.get('password')?.valueChanges.subscribe(() => {
        this.isPasswordValid = this.newUserForm.get('password')?.valid ?? false;
        this.isPasswordMatch = true;
        this.cdr.markForCheck();
      });
    }
  }

  close(){
    this.router.navigate(["/login"])
  }
}
