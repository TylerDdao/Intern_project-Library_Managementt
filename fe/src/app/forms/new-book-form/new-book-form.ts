import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { GenreService } from '../../services/genre-service/genre-service';
import { Genre } from '../../models/genre';
import { Page } from '../../models/page';
import { isPlatformBrowser } from '@angular/common';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { LanguageService } from '../../services/language-service/language-service';

@Component({
  selector: 'app-new-book-form',
  imports: [ReactiveFormsModule, TranslateModule, PagesComponent],
  templateUrl: './new-book-form.html',
  styleUrl: './new-book-form.css',
})
export class NewBookForm {
@Output() onClose = new EventEmitter<void>();

  selectedFile: File | null = null;
  preview: string | null = null;

  genres: Genre[] = []
  genresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  }
  chosenGenres: Genre[] = []


  constructor(
    private cdr: ChangeDetectorRef,
    private genreService: GenreService,
    private translate: TranslateService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  newBookForm = new FormGroup({
    title: new FormControl('', Validators.required),
    author: new FormControl('', Validators.required),
    copies: new FormControl('', Validators.required),
    genres: new FormControl<string[]>([], Validators.required),
  });

  // handleAddGenre(genre: Genre){
  //   this.genreService.createGenre(genre).subscribe({
  //     next: (data:any)=>{
  //       if(data.code == "200"){
  //         this.genres.push(genre)
  //         this.cdr.markForCheck();
  //       }
  //     },
  //     error:(err)=>{
  //       console.error(err)
  //     }
  //   })
  // }

  toggleChosenGenre(selectedGenre: Genre) {
    if (this.chosenGenres.includes(selectedGenre)) {
      this.chosenGenres = this.chosenGenres.filter(genre => genre !== selectedGenre);
    } else {
      this.chosenGenres.push(selectedGenre);
    }
    this.cdr.markForCheck();
  }

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

  onSubmit(){
    console.log("Submit")
  }

  close(): void {
    this.onClose.emit();
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }

  handleClearBookCover(){
    this.selectedFile = null
    this.preview = null
    this.cdr.markForCheck()
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      this.selectedFile = input.files[0];

      const reader = new FileReader();
      reader.onload = () => {
        this.preview = reader.result as string;
        this.cdr.markForCheck();
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchGenre()
    }
  }
}
