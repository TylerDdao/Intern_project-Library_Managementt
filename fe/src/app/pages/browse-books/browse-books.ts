import { Component } from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language-service/language-service';
import { NavbarComponent } from '../../components/navbar/navbar';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { Book } from '../../models/book';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-browse-books',
  imports: [NavbarComponent, SortSideBarComponent, BookCardComponent, TranslateModule],
  templateUrl: './browse-books.html',
  styleUrl: './browse-books.css',
})
export class BrowseBooks {
  constructor(
    public langService: LanguageService,
    private authService: AuthService,
    protected router: Router) 
  {}

  book:Book = {
    bookName: "Project Hail Mary",
    author: "Andy Weir",
    genres: ["Sci-fi"]
  }

  handleApply(query: SideBarQuery): void {
    // do whatever you want here
    alert("Sort by: " + query.sortBy)
    alert("Filter by: " + query.filterBy)
    alert("Search query: " + query.searchQuery)
  }
}
