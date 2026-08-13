import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { LanguageService } from '../../services/language-service/language-service';
import { LanguageSelector } from '../../components/language-selector/language-selector';

@Component({
  selector: 'app-unauthorized-page',
  imports: [TranslateModule, LanguageSelector],
  templateUrl: './unauthorized-page.html',
  styleUrl: './unauthorized-page.css',
})
export class UnauthorizedPage {
  constructor(
    private router:Router,
    private translate: TranslateService,
    @Inject(PLATFORM_ID) private platformId: Object,
    protected langService: LanguageService, 
  ){}

  backToLogin: string = "Back to login"
  navigateTologin(){
    this.router.navigate(["/login"])
  }

}
