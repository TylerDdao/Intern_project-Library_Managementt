import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators, FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { GenreService } from '../../services/genre-service/genre-service';
import { Genre } from '../../models/genre';
import { Page } from '../../models/page';
import { isPlatformBrowser } from '@angular/common';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { LanguageService } from '../../services/language-service/language-service';
import { BookService } from '../../services/book-service/book-service';
import { LoadingComponent } from "../../components/loading-component/loading-component";
import { errorNoti } from '../../util/error-notification';
import { HttpErrorResponse } from '@angular/common/http';
import { Book } from '../../models/book';

@Component({
  selector: 'app-new-book-form',
  imports: [ReactiveFormsModule, TranslateModule, PagesComponent, LoadingComponent, FormsModule],
  templateUrl: './new-book-form.html',
  styleUrl: './new-book-form.css',
})
export class NewBookForm {
@Output() onClose = new EventEmitter<void>();
@Output() onChange = new EventEmitter<void>();

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

  isLoading: {[key:string]: boolean} = {
    "createBook": false,
    "loadGenres": false,
    "loadSearch": false
  }

  isValid: { [key: string]: boolean } = {
    title: true,
    author: true,
    copies: true
  };

  query: string = ''
  isSearch: boolean = false
  resultGenres: Genre[] = []
  resultGenresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  }

  constructor(
    private cdr: ChangeDetectorRef,
    private genreService: GenreService,
    @Inject(PLATFORM_ID) private platformId: Object,
    private bookService: BookService,
    private translate: TranslateService
  ){}

  newBookForm = new FormGroup({
    title: new FormControl('', Validators.required),
    author: new FormControl('', Validators.required),
    copies: new FormControl(0, [Validators.required,Validators.min(0)]),
  });

  toggleChosenGenre(selectedGenre: Genre) {
    if (this.chosenGenres.includes(selectedGenre)) {
      this.chosenGenres = this.chosenGenres.filter(genre => genre !== selectedGenre);
    } else {
      this.chosenGenres.push(selectedGenre);
    }
    this.cdr.markForCheck();
  }

  fetchGenre(page: Page = this.genresPage){
    this.isLoading["loadGenres"]=true;
    this.genreService.getAllGenres(page.number).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.genres = data.data.content
          this.cdr.markForCheck();
        }
        this.isLoading["loadGenres"]=false
        this.cdr.markForCheck()
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading["loadGenres"]=false
        this.cdr.markForCheck()
      }
    })
  }

  onSubmit() {
    this.newBookForm.markAllAsTouched();

    this.isValid['title'] = this.newBookForm.get('title')?.valid ?? false;
    this.isValid['author'] = this.newBookForm.get('author')?.valid ?? false;

    this.isValid['copies'] = this.newBookForm.get('copies')?.valid ?? false;

    if (this.newBookForm.invalid) {
      return;
    }

    this.isLoading["createBook"] = true;

    const bookData: Book = {
      id: 0, // Backend should generate the real ID
      title: this.newBookForm.get('title')?.value ?? '',
      author: this.newBookForm.get('author')?.value ?? '',
      copies: this.newBookForm.get('copies')?.value ?? 0,
      genres: this.chosenGenres
    };
    this.bookService.createBook(bookData).subscribe({
      next: (data: any) => {
        if (data.code !== '200') {
          this.isLoading["createBook"] = false;
          return;
        }
        const savedBook: Book = data.data;
        if (!this.selectedFile) {
          const message = this.translate.instant("newBookForm.Book-is-added")
          alert(message)
          this.onChange.emit();
          this.close();
          this.isLoading["createBook"] = false;
          return;
        }
        this.bookService.uploadBookCover(savedBook.id, this.selectedFile).subscribe({
          next: (uploadData: any) => {
            if (uploadData.code === '200') {
              const message = this.translate.instant("newBookForm.Book-is-added")
              alert(message)
              this.onChange.emit();
              this.close();
            } else {
              this.isLoading["createBook"] = false;
            }
          },
          error: (err: HttpErrorResponse) => {
            errorNoti(err, this.translate);
            this.isLoading["createBook"] = false;
          }
        });
      },
      error: (err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
        this.isLoading["createBook"] = false;
      }
    });
  }

  close(): void {
    this.onClose.emit();
  }

  handleClearBookCover(){
    this.selectedFile = null
    this.preview = null
    this.cdr.markForCheck()
  }

  MAXIMUM_FILE: number = 500

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      this.selectedFile = input.files[0];

      const maxSizeBytes = this.MAXIMUM_FILE * 1024;

      if (this.selectedFile.size > maxSizeBytes) {
        const fileSizeLabel = this.translate.instant("newBookForm.Your-file-size-is");
        const message = this.translate.instant('newBookForm.File-too-large');
        alert(`${message}\n${fileSizeLabel}: ${(this.selectedFile.size / 1024).toFixed(2)} KB`);
        input.value = '';
        return;
      }

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

      this.newBookForm.get('title')?.valueChanges.subscribe(() => {
        this.isValid["title"] = true
 
      });
      this.newBookForm.get('author')?.valueChanges.subscribe(() => {
        this.isValid["author"] = true

      });
      this.newBookForm.get('copies')?.valueChanges.subscribe(() => {
        this.isValid["copies"] = true
        
      });
    }
  }

  handleSearchGenres(){
    if(this.query === '') return
    this.isLoading["loadSearch"] = true
    this.genreService.getGenresByName(this.query, this.genresPage.number).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.resultGenres = data.data.content
          this.resultGenresPage = data.data
          this.isSearch = true
        }
        this.isLoading["loadSearch"] = false
        this.cdr.markForCheck();
      },
      error: (err:HttpErrorResponse)=>{
        errorNoti(err, this.translate)
        this.isLoading["loadSearch"] = false;
      }
    })
  }

  handleClearSearch(){
    this.isSearch = false
    this.query = '';
    this.cdr.markForCheck();
  }

}
