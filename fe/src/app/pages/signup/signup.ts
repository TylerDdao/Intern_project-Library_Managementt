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

@Component({
  selector: 'app-signup',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  invalidInformation = false;
  signupCompleted = false;
  user!:User

  constructor(
    private authService: AuthService,
    public langService: LanguageService,
    protected router: Router,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    private userService: UserService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) 
  {}

  isUsernameInvalid: boolean = false;

  isVerifyingEmail:boolean = false;
  isEmailVerified:boolean=false;
  isEmailInvalid: boolean = false;
  isSendingVerificationEmail:boolean = false;

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(/^(0|\+84)[0-9]{9}$/)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    address: new FormControl(''),
    verificationCode: new FormControl('', Validators.required)
  });

  handleSendVerificationCode(){
    this.isVerifyingEmail = true;
    this.isSendingVerificationEmail = true;
    const email = this.newUserForm.get("email")?.value?.trim().toLowerCase();
    if (!email) {
      return;
    }
    const name = this.translate.instant("user.New-user")
    this.authService.sendVerificationCode(email, name).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          const message = this.translate.instant("verification.Your-verification-code-has-been-sent-to-your-email");
          alert(message)
          this.isSendingVerificationEmail = false;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        const message = this.translate.instant("verification.There-is-an-error-while-sending-verification-code");
        alert(message)
        console.error(err)
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
          const message = this.translate.instant("verification.Your-email-has-been-verified");
          alert(message)
          this.isEmailVerified = true;
          this.isEmailInvalid = false;
          this.newUserForm.get('verificationCode')?.disable();
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        const message = this.translate.instant("verification.There-is-an-error-while-verifying-your-email");
        alert(message)
        console.error(err)
      }
    })
  }

  onSubmit(){
    console.log("Submit")
  }

  ngOnInit(){
    if (isPlatformBrowser(this.platformId)) {
      // this.newUserForm.get('username')?.valueChanges.subscribe(() => {
      //   this.isUsernameAvailable = null;
      //   this.isUsernameInvalid = false;
      //   this.cdr.markForCheck();
      // });
      this.newUserForm.get('email')?.valueChanges.subscribe(() => {
        this.isEmailInvalid = false;
        this.isEmailVerified = false;
        this.newUserForm.get('verificationCode')?.enable();
        this.cdr.markForCheck();
      });
    }
  }

  close(){
    this.router.navigate(["/login"])
  }
}
