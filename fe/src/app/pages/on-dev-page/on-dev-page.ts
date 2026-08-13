import { Component } from '@angular/core';
import { LanguageSelector } from "../../components/language-selector/language-selector";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-on-dev-page',
  imports: [LanguageSelector, TranslateModule],
  templateUrl: './on-dev-page.html',
  styleUrl: './on-dev-page.css',
})
export class OnDevPage {}
