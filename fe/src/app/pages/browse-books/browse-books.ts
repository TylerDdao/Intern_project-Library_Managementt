import { Component } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-browse-books',
  imports: [],
  templateUrl: './browse-books.html',
  styleUrl: './browse-books.css',
})
export class BrowseBooks {
  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router) 
  {}
}
