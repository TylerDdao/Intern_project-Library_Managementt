import { Component } from '@angular/core';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-language-selector',
  imports: [],
  templateUrl: './language-selector.html',
  styleUrl: './language-selector.css',
})
export class LanguageSelector {
  constructor(
    protected langService: LanguageService
  ){}
}
