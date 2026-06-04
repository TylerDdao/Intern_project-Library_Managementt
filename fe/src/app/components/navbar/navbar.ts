import { NgClass } from '@angular/common';
import { Component } from '@angular/core';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [TranslateModule, NgClass],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  currentLang = 'en';

  constructor(private translate: TranslateService) {}

  switchLanguage(lang: string) {
    this.translate.use(lang);
    this.currentLang = lang;
  }
}