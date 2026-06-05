import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth-service';
import { LanguageService } from '../../services/language-service/language-service';
import { RouteReuseStrategy } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: RouteReuseStrategy) 
  {}
}
