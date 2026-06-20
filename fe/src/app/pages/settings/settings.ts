import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { LanguageService } from '../../services/language-service/language-service';
import { Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-settings',
  imports: [TranslateModule, NavbarComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class Settings {
  invalidInformation = false;
  signupCompleted = false;

  constructor(
    private authService: AuthService,
    public langService: LanguageService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) 
  {}

  accountUpdateForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.email, Validators.required]),

    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', [Validators.pattern(/^(\+84|0)[0-9]{9}$/) , Validators.required]),

    province: new FormControl(''),
    city: new FormControl(''),
    addressLine1: new FormControl(''),
    addressLine2: new FormControl('')
  })

  onSubmit() {
    if (this.accountUpdateForm.invalid){
      this.invalidInformation = true;
      return;
    };

    const { username, password, email, fullName, phoneNumber, province, city, addressLine1, addressLine2 } = this.accountUpdateForm.value;
    
  }

  get userName(): string {
    if(isPlatformBrowser(this.platformId)){
      const user = localStorage.getItem("user");
      return user ? JSON.parse(user).username : 'NULL';
    }
    return ""
  }

  get email(): string {
    if(isPlatformBrowser(this.platformId)){
      const user = localStorage.getItem("user");
      return user ? JSON.parse(user).email : 'NULL';
    }
    return ""
  }

  get phoneNumber(): string {
    if(isPlatformBrowser(this.platformId)){
      const user = localStorage.getItem("user");
      return user ? JSON.parse(user).phoneNumber : 'NULL';
    }
    return ""
  }

  get fullName(): string {
    if(isPlatformBrowser(this.platformId)){
      const user = localStorage.getItem("user");
      return user ? JSON.parse(user).fullName : 'NULL';
    }
    return ""
  }
}
