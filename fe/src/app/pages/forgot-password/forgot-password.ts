import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth-service';
import { Router, RouterLink } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';

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
    protected router: Router) 
  {}

  isSendingEmail:boolean = false;

  resetPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  close(){
    this.router.navigate(['/login'])
  }

  onSubmit(){

  }
}
