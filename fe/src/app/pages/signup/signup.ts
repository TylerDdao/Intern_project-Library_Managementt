import { Component, NgZone } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-signup',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  invalidInformation = false;
  signupCompleted = false;

  countdown = 5;
  // private countdownSub: Subscription | null = null;

  constructor(
    private authService: AuthService,
    public langService: LanguageService,
    protected router: Router,
  ) 
  {}

  // startCountdown() {
  //   this.signupCompleted = true;
  //   this.countdownSub = interval(1000).subscribe(() => {
  //     this.countdown--;
  //     if (this.countdown === 0) {
  //       this.countdownSub?.unsubscribe();
  //       this.router.navigate(['/login']);
  //     }
  //   });
  // }

  async signup(username: string, password: string, email: string, fullName: string, phoneNumber: string, province: string, city: string, addressLine1: string, addressLine2: string){
    this.authService.signup(username, password, email, fullName, phoneNumber, province, city, addressLine1, addressLine2).subscribe({
      next: (data: any) => {
        console.log(data)
        if(data.code == 200){
          this.invalidInformation = false
          this.signupCompleted = true
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
    email: new FormControl('', Validators.email),

    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.pattern(/^(\+84|0)[0-9]{9}$/) , Validators.required]),

    province: new FormControl(''),
    city: new FormControl(''),
    addressLine1: new FormControl(''),
    addressLine2: new FormControl('')
  })

  onSubmit() {
    if (this.signupForm.invalid){
      this.invalidInformation = true;
      return;
    };

    const { username, password, email, fullName, phoneNumber, province, city, addressLine1, addressLine2 } = this.signupForm.value;
    
    this.signup(username ?? '', password ?? '', email ?? '', fullName ?? '', phoneNumber ?? '', province ?? '', city ?? '', addressLine1 ?? '', addressLine2 ?? '')
  }
}
