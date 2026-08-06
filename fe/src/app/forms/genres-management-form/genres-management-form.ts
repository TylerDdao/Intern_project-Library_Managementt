import { ChangeDetectorRef, Component, EventEmitter, Inject, Output, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Genre } from '../../models/genre';
import { Page } from '../../models/page';
import { GenreService } from '../../services/genre-service/genre-service';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { isPlatformBrowser } from '@angular/common';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';

@Component({
  selector: 'app-genres-management-form',
  imports: [FormsModule, TranslateModule, ReactiveFormsModule],
  templateUrl: './genres-management-form.html',
  styleUrl: './genres-management-form.css',
})
export class GenresManagementForm {
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<Genre[]>();

  isLoading: boolean = true
  genres: Genre[] = []
  genresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1,
    totalElements: 0,
    numberOfElements: 0
  }

  removedGenres: Genre[] = []

  constructor(
    private genreService: GenreService,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ){}

  newGenreForm = new FormGroup({
    genreName: new FormControl('', Validators.required),
  });

  query:string = ''
  isSearch:boolean = false
  searchGenres: Genre[] = []
  searchGenresPage: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1,
    totalElements: 0,
    numberOfElements: 0
  }
  handleSearch(){
    if (!this.query.trim()) {
      return;
    }

    this.isSearch = true;
    this.isLoading = true;
    this.searchGenres = [];
    this.searchGenresPage.number = 0;

    this.genreService.getGenresByName(this.query, 0, 10).subscribe({
      next: (data: any) => {
        if (data.code === "200") {
          this.searchGenres = data.data.content;
          this.searchGenresPage = data.data;
        }
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  handleClearSearch(){
    this.genres = this.filterRemovedGenres(this.genres);
    this.isSearch = false;
    this.query =""
    this.cdr.markForCheck()
  }

  handleSearchMoreGenre() {
    this.searchGenresPage.number++;
    this.isLoading = true;

    this.genreService
      .getGenresByName(this.query, this.searchGenresPage.number, 10)
      .subscribe({
        next: (data: any) => {
          if (data.code === "200") {
            this.searchGenres.push(
              ...data.data.content
            );

            this.searchGenresPage = data.data;
          }

          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (err: HttpErrorResponse) => {
          errorNoti(err, this.translate);
          this.isLoading = false;
          this.cdr.markForCheck();
        }
      });
  }

  toggleDelete(genre: Genre) {
    if(!this.isSearch){
      if (this.removedGenres.some(g => g.id === genre.id)) {
        this.removedGenres = this.removedGenres.filter(g => g !== genre);
        this.genres.push(genre)
      } else {
        this.removedGenres.push(genre);
        this.genres = this.genres.filter(g => g !== genre);
      }
      if(this.removedGenres.length > (this.genresPage.numberOfElements ?? 10)){
        this.genresPage.last = false;
      }
    }
    else{
      if (this.removedGenres.some(g => g.id === genre.id)) {
        this.removedGenres = this.removedGenres.filter(g => g !== genre);
      } else {
        this.removedGenres.push(genre);
      }
      if(this.removedGenres.length > (this.searchGenresPage.numberOfElements ?? 10)){
        this.searchGenresPage.last = false;
      }
    }
    this.cdr.markForCheck()
  }

  handleClear(){
    this.newGenreForm.patchValue({
      genreName: ""
    })
    this.cdr.markForCheck();
  }


  private filterRemovedGenres(genres: Genre[]): Genre[] {
    return genres.filter(
      genre => !this.removedGenres.some(r => r.id === genre.id)
    );
  }

  handleMoreGenre(){
    this.genresPage.number++;
    this.isLoading=true;
    this.genreService.getAllGenres(this.genresPage.number, 10).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.genres.push(...this.filterRemovedGenres(data.data.content))
          this.isLoading=false;
          this.genresPage = data.data;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        console.error(err)
      }
    })
  }

  handleLessGenre(){
    this.genresPage.number = 0;
    this.isLoading=true;
    this.genreService.getAllGenres(this.genresPage.number, 10).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.genres = this.filterRemovedGenres(data.data.content);
          this.genres.filter
          this.isLoading=false;
          this.genresPage = data.data;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        console.error(err)
      }
    })
  }

  fetchGenre(){
    this.isLoading=true;
    this.genreService.getAllGenres(this.genresPage.number, 10).subscribe({
      next: (data: any)=>{
        if(data.code == "200"){
          this.genres.push(...this.filterRemovedGenres(data.data.content))
          this.isLoading=false;
          this.genresPage = data.data;
          this.cdr.markForCheck();
        }
      },
      error: (err)=>{
        console.error(err)
      }
    })
  }

  save(){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message+"?")
    if(option){
      this.onChange.emit(this.removedGenres);
    }
  }

  onSubmit(){
    const {genreName} = this.newGenreForm.value
    if(genreName){
      const genre:Genre = {
        name: genreName
      }
      this.genreService.createGenre(genre).subscribe({
        next:(data:any)=>{
          if(data.code == "200"){
            const message = this.translate.instant("genresManagement.Genre-is-added")
            alert(message)
            this.fetchGenre();

          }
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
        }
      })
    }
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
