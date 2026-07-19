import { ChangeDetectorRef, Component, Inject, NgZone, OnChanges, PLATFORM_ID, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar';
import { Book } from '../../models/book';
import { BookService } from '../../services/book-service/book-service';
import { BookCardComponent } from '../../components/book-card-component/book-card-component';
import { TranslateModule } from '@ngx-translate/core';
import { Post } from '../../models/post';
import { PostService } from '../../services/post-service/post-service';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { ChangeDetectionStrategy } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { Borrow } from '../../models/borrow';


@Component({
  selector: 'app-book-page',
  imports: [NavbarComponent, PostCardComponent, TranslateModule],
  templateUrl: './book-page.html',
  styleUrl: './book-page.css',
  changeDetection: ChangeDetectionStrategy.Default
})
export class BookPage{
  bookId!: number;
  book!: Book;
  bookCover = '';

  isLoadingPosts: boolean = true;
  posts: Post[] = [];

  isBorrowing: boolean = true;
  borrows: Borrow[] = []
  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private postService: PostService,
    private borrowService: BorrowService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  //TODO CHECK IF THE BOOK IS ALREADY BORROWED

  handleBorrow(){
    if(this.book){
      const dueDate = new Date();
      dueDate.setDate(dueDate.getDate() + 14);
      this.borrowService.createBorrow(this.book, dueDate.toISOString()).subscribe({
        next: (data:any)=>{
          if(data.code == "200"){
            alert("Borrow created")
            alert("Borrow dues on: " + new Date(data.data.dueDate).toDateString())
          }
        },
        error: (err) => {
          alert(err.message)
        }
      })
    }
  }

  fetchPosts(id: number):void{
    this.postService.getPostsByBookId(id).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.posts = data.data.content;
          this.isLoadingPosts = false;
          this.cdr.markForCheck(); 
        }
      },
      error: (err) => { 
        console.error(err);
      }
    })
  }

  fetchBook(id: number):void{
    this.bookService.getBookById(id).subscribe({
      next: (data: any) => {
        if (data.code == "200") {
          this.ngZone.run(() => {  // ✅ forces Angular to detect changes
            this.book = data.data;
            this.bookCover = this.book.title.replaceAll(' ', '-').toLowerCase();
            this.cdr.markForCheck();
          });
        }
      },
      error:(err)=>{
        console.error(err)
      }
    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.bookId = Number(this.route.snapshot.paramMap.get('book-id'));
      this.fetchBook(this.bookId);
      this.fetchPosts(this.bookId);
    }
  }

  onImageError(event: Event): void {
    (event.target as HTMLImageElement).src = '/book-covers/default.jpg';
  }
}
