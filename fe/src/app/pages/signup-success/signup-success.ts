import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-signup-success',
  imports: [NavbarComponent, TranslateModule],
  templateUrl: './signup-success.html',
  styleUrl: './signup-success.css',
})
export class SignupSuccess {
  constructor(
    private router:Router
  ){}

  handleBackToLogin(){
    this.router.navigate(['/login'])
  }
}
