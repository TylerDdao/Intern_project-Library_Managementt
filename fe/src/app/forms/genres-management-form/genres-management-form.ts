import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Genre } from '../../models/genre';
import { Page } from '../../models/page';
import { GenreService } from '../../services/genre-service/genre-service';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { TranslateModule } from '@ngx-translate/core';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-genres-management-form',
  imports: [NavbarComponent, PagesComponent, TranslateModule],
  templateUrl: './genres-management-form.html',
  styleUrl: './genres-management-form.css',
})
export class GenresManagementForm {
  @Output() onClose = new EventEmitter<void>();

  genres: Genre[] = []
  genresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  }

  constructor(
    private genreService: GenreService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  fetchGenre(page: Page = this.genresPage){
    this.genreService.getAllGenres(page.number).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.genres = data.data.content
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        console.error(err)
      }
    })
  }

  close(): void {
    this.onClose.emit();
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchGenre()
    }
  }
}
