import { ChangeDetectorRef, Component, ElementRef, Inject, inject, PLATFORM_ID, ViewChild } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { environment } from '../../../environments/environment';

declare const turnstile: any;

@Component({
  selector: 'app-forgot-password',
  imports: [TranslateModule,NavbarComponent, ReactiveFormsModule, LoadingComponent],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  @ViewChild('turnstileContainer') turnstileContainer!: ElementRef<HTMLDivElement>;
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

  turnstileToken: string | null = null;
  private turnstileWidgetId: string | null = null;

  ngAfterViewInit(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    this.renderTurnstile();
  }

  private renderTurnstile(attempt = 0): void {
    if (typeof turnstile !== 'undefined') {
      this.turnstileWidgetId = turnstile.render(this.turnstileContainer.nativeElement, {
        sitekey: environment.turnstileSitekey,
        theme: 'light',
        callback: (token: string) => {
          this.turnstileToken = token;
          this.cdr.markForCheck();
        },
      });
      return;
    }
    if (attempt < 20) {
      setTimeout(() => this.renderTurnstile(attempt + 1), 100);
    }
  }

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
    if(email && this.resetPasswordForm.get("email")?.valid && this.turnstileToken){
      this.isEmailValid = true;
      this.isSendingEmail = true
      this.authService.sendResetPasswordLink(email, this.turnstileToken).subscribe({
        next: (data: any)=>{
          if(data.code == "200"){
            const message = this.translate.instant('email.Email-has-been-sent')
            alert(message)
            this.isSendingEmail = false;
            this.cdr.markForCheck();
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
