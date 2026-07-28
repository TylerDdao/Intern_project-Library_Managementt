import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { Post } from '../../models/post';
import { Router } from '@angular/router';
import { PostService } from '../../services/post-service/post-service';
import { LanguageService } from '../../services/language-service/language-service';
import { isPlatformBrowser } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { Page } from '../../models/page';

@Component({
  selector: 'app-my-posts',
  imports: [SortSideBarComponent, NavbarComponent, PostCardComponent, TranslateModule, PagesComponent],
  templateUrl: './my-posts.html',
  styleUrl: './my-posts.css',
})
export class MyPosts {
  posts:Post[] =[];
  postPages: Page = {
    first: true,
    last: true,
    number: 0,
    totalPages: 1
  };

  searchPost: Post[] = []
  isSearch:boolean = false;
  isPostFound:boolean = true;

  isLoading: {[key:string]:boolean} = {
    "posts": true,
    "search": true
  };

  constructor(
    public langService: LanguageService,
    private postService: PostService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) 
  {}

  handleNavigateCreatePost(){
    this.router.navigate(['/create-post'])
  }

  fetchMyPosts(page: Page = this.postPages): void {
    this.isLoading["posts"] = true
    this.postService.getMyPosts(page.number).subscribe({
        next: (data: any) => {
            if (data.code == "200") {
                this.posts = data.data.content;
                this.postPages = data.data;
                this.isLoading["posts"] = false;
                this.cdr.markForCheck();
            }
        },
        error: (err) => console.error(err)
    });
  }

  handleLessPosts():void{
    this.postService.getMyPosts(0).subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.postPages = data.data;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  handleLoadMorePosts():void{
    this.postPages.number ++;
    this.postService.getMyPosts(this.postPages.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.posts.push(...data.data.content);
          this.postPages = data.data;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  handleApply(query: SideBarQuery): void {
    this.isLoading["search"] = true
    if(query.isClear){
      this.isSearch = false;
      this.searchPost = [];
      this.isPostFound = false;
      this.cdr.markForCheck();
      return;
    }
    this.isSearch = true;
    this.searchPost = [];
    this.isPostFound = true;
    this.postService.getPostsByQuery(query).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          if(data.data.totalElements > 0){
            this.searchPost = data.data.content;
            this.cdr.markForCheck();
          }
          else{
            this.isPostFound = false;
            this.cdr.markForCheck();
          }
          this.isLoading["search"] = false;
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchMyPosts();
    }
  }

  onPostLikeToggled(updatedPost: Post) {
    const index = this.posts.findIndex(p => p.id === updatedPost.id);
    if (index !== -1) {
        this.posts[index] = updatedPost;
        this.cdr.markForCheck();
    }

    const searchIndex = this.searchPost.findIndex(p => p.id === updatedPost.id);
    if (searchIndex !== -1) {
        this.searchPost[searchIndex] = updatedPost;
        this.cdr.markForCheck();
    }
  }
}
