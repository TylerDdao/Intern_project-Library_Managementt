import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth-service';
import { LanguageService } from '../../services/language-service/language-service';
import { RouteReuseStrategy } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, PostCardComponent],
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
