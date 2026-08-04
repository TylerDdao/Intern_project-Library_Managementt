import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-not-found',
  imports: [TranslateModule],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css',
})
export class NotFound {
  constructor(private translate: TranslateService) {
    if (!translate.currentLang) {
      translate.use(translate.defaultLang || 'en'); 
    }
  }

}
