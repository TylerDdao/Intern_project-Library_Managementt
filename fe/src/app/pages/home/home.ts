import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { Router } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { Post } from '../../models/post';
import { PostsService } from '../../services/posts-service/posts-service';
import { isPlatformBrowser } from '@angular/common';
import { BorrowService } from '../../services/borrow-service/borrow-service';
import { Borrow } from '../../models/borrow';
import { MiniPostCardComponent } from '../../components/mini-post-card-component/mini-post-card-component';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, MiniPostCardComponent, BorrowCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  posts:Post[] = [];
  borrows:Borrow[] = [];

  constructor(
    public langService: LanguageService,
    private postsService: PostsService,
    private borrowService: BorrowService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) 
  {}

  fetchBorrowsByUserId():void{
    const userId = JSON.parse(localStorage.getItem("user") ?? "{}").id
    if (!userId) return;
    this.borrowService.getBorrowsByUserId(userId).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.borrows = data.data.content;
          console.log('borrows:', data);
          this.cdr.markForCheck();
        }
      }
    })
  }

  fetchAllPosts():void{
    this.postsService.getAllPost().subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  ngOnInit() {
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllPosts()
      this.fetchBorrowsByUserId()
    }
  }

  onPostLikeToggled(updatedPost: Post) {
    const index = this.posts.findIndex(p => p.id === updatedPost.id);
    if (index !== -1) {
        this.posts[index] = updatedPost;
        this.cdr.markForCheck();
    }
  }
}
