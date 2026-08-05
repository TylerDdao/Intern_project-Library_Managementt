import { ChangeDetectorRef, Component, Inject, inject, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [TranslateModule,NavbarComponent, ReactiveFormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router,
    private translate: TranslateService,
    @Inject(PLATFORM_ID) private platformId:Object,
    private cdr: ChangeDetectorRef) 
  {}

  isSendingEmail:boolean = false;
  isEmailValid:boolean = true;

  resetPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  close(){
    this.router.navigate(['/login'])
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.resetPasswordForm.get("email")?.valueChanges.subscribe(()=>{
        this.isEmailValid = true;
        this.cdr.markForCheck();
      })
    }
  }

  onSubmit(){
    const {email} = this.resetPasswordForm.value
    if(email && this.resetPasswordForm.get("email")?.valid){
      this.isEmailValid = true;
      this.isSendingEmail = true
      this.authService.sendResetPasswordLink(email).subscribe({
        next: (data: any)=>{
          if(data.code == "200"){
            const message = this.translate.instant('email.Email-has-been-sent')
            alert(message)
            this.isSendingEmail = false;
          }
        },
        error: (err:HttpErrorResponse)=>{
          errorNoti(err, this.translate);
          this.isSendingEmail = false;
          this.cdr.markForCheck()
        }
      })
    }
    else{
      this.isEmailValid = false;
    }
  }
}
