import { ChangeDetectorRef, Component, NgZone } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { LanguageService } from '../../services/language-service/language-service';
import { User } from '../../models/user';

@Component({
  selector: 'app-signup',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  invalidInformation = false;
  signupCompleted = false;
  user:User = {
    id: 0,
    fullName: "",
    email: "",
    phoneNumber: "",
    address: "",
    role: "",
    username: ""
  };

  constructor(
    private authService: AuthService,
    public langService: LanguageService,
    protected router: Router,
    private cdr: ChangeDetectorRef
  ) 
  {}

  async signup(user: User){
    this.authService.signup(user).subscribe({
      next: (data: any) => {
        console.log(data)
        if(data.code == 200){
          this.invalidInformation = false
          this.signupCompleted = true
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        this.invalidInformation = true
        console.error(err);
      }
    })
  }

  signupForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.email, Validators.required]),

    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.pattern(/^(\+84|0)[0-9]{9}$/) , Validators.required]),

    address: new FormControl(''),
  })

  onSubmit() {
    if (this.signupForm.invalid){
      this.invalidInformation = true;
      return;
    };

    const { username, password, email, fullName, phoneNumber, address } = this.signupForm.value;
    
    this.user.username = username ?? '';
    this.user.password = password ?? '';
    this.user.address = address ?? '';
    this.user.email = email ?? '';
    this.user.fullName = fullName ?? 'NULL';
    this.user.phoneNumber = phoneNumber ?? 'NULL';
    this.user.role = "ROLE_USER"

    
    this.signup(this.user)
  }
}
