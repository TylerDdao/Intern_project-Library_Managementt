import { Component } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { LanguageSelector } from '../../components/language-selector/language-selector';

@Component({
  selector: 'app-not-found',
  imports: [TranslateModule, LanguageSelector],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css',
})
export class NotFound {
  constructor(
    protected langService: LanguageService
  ){}
}
