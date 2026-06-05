import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  currentLang: string;

  constructor(
    private translate: TranslateService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    const saved = isPlatformBrowser(this.platformId) 
      ? localStorage.getItem('lang') 
      : null;
    this.currentLang = saved ?? 'en';
    translate.use(this.currentLang);
  }

  switchLanguage(lang: string) {
    this.currentLang = lang;
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('lang', lang);
    }
    this.translate.use(lang);
  }
}