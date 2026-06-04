import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [TranslateModule, NavbarComponent],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  currentLang = 'en';
  wrongCredential = false;

  constructor(
    private translate: TranslateService, 
    private authService: AuthService,
    private router: Router) 
  {
    translate.use('en');
  }

  async login(username: string, password: string){
    this.authService.login(username, password).subscribe({
      next: (data: any) => {
        console.log(data)
        if(data.code == 200){
          localStorage.setItem("token", data.data.token);
          this.router.navigate(['/home'])
        }
      },
      error: (err) => {
        this.wrongCredential = true
        console.error(err);
      }
    })
  }

  switchLanguage(lang: string) {
    this.translate.use(lang);
    this.currentLang = lang;
  }
}
