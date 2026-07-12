import { ChangeDetectorRef, Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { AuthService } from '../../services/auth-service';
import { Router, RouterLink } from '@angular/router';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-login',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  wrongCredential = false;

  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router,
    private cdr: ChangeDetectorRef) 
  {}

  async login(username: string, password: string){
    this.authService.login(username, password).subscribe({
      next: (data: any) => {
        console.log(data)
        if(data.code == 200){
          sessionStorage.setItem("token", data.data.token);
          sessionStorage.setItem("user", JSON.stringify(data.data.user));
          sessionStorage.setItem("authorities", JSON.stringify(data.data.authorities));
          this.router.navigate(['/home']);
        }
      },
      error: (err) => {
        this.wrongCredential = true
        this.cdr.markForCheck()
        console.error(err);
      }
    })
  }

  loginForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  onSubmit() {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.value;
    
    this.login(username ?? '', password ?? '')
  }
}
