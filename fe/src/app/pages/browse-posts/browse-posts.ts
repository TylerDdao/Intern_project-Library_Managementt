import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule } from '@ngx-translate/core';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { PostCardComponent } from "../../components/post-card-component/post-card-component";
import { Post } from '../../models/post';
import { LanguageService } from '../../services/language-service/language-service';
import { PostService } from '../../services/post-service/post-service';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { PagesComponent } from '../../components/pages-component/pages-component';
import { Page } from '../../models/page';
import { LoadingComponent } from '../../components/loading-component/loading-component';
import { AnnouncementService } from '../../services/announcement-service/announcement-service';
import { Announcement } from '../../models/announcement';
import { AnnouncementComponent } from "../../components/announcement-component/announcement-component";

@Component({
  selector: 'app-browse-posts',
  imports: [NavbarComponent, TranslateModule, SortSideBarComponent, PostCardComponent, PagesComponent, LoadingComponent, AnnouncementComponent],
  templateUrl: './browse-posts.html',
  styleUrl: './browse-posts.css',
})
export class BrowsePosts {
  isLoadPosts: boolean = true;
  posts:Post[] =[];
  postPages:Page = {
  totalPages: 1,
  number: 0,
  last: true,
  first: true
  }
  lastQuery: SideBarQuery | null = null;

  searchPost: Post[] = []
  isSearch:boolean = false;
  isPostFound:boolean = true;

  constructor(
    public langService: LanguageService,
    private postService: PostService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
    private announcementService: AnnouncementService
    
  ) 
  {}
  announcements: Announcement[] = []
  handleCloseAnnouncement(id: number) {
    this.announcementService.closeAnnouncement(id);
  }

  handleNavigateCreatePost(){
    this.router.navigate(['/create-post'])
  }

  fetchAllPosts(page:Page = this.postPages):void{
    this.postService.getAllPost(page.number).subscribe({
      next: (data:any) => {
        if(data.code == "200"){
          this.posts = data.data.content;
          this.postPages = data.data;
          this.isLoadPosts = false;
          this.cdr.markForCheck();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  handleLessPosts():void{
    this.postService.getAllPost(0).subscribe({
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
    this.postService.getAllPost(this.postPages.number).subscribe({
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

  fetchSearchPosts(page: Page): void {
    // re-run the last query with the new page
    if (this.lastQuery) {
        this.postService.getPostsByQuery(this.lastQuery, page.number).subscribe({
            next: (data: any) => {
                if (data.code == "200") {
                    this.searchPost = data.data.content;
                    this.postPages = data.data;
                    this.cdr.markForCheck();
                }
            },
            error: (err) => console.error(err)
        });
    }
}

  handleApply(query: SideBarQuery): void {
    if(query.isClear){
      this.isSearch = false;
      this.searchPost = [];
      this.isPostFound = false;
      this.cdr.markForCheck();
      return;
    }
    this.lastQuery = query;
    this.isSearch = true;
    this.searchPost = [];
    this.isPostFound = true;
    this.postService.getPostsByQuery(query).subscribe({
      next: (data: any) => {
        if(data.code == "200"){
          if(data.data.totalElements > 0){
            this.searchPost = data.data.content;
            this.postPages = data.data;
            this.cdr.markForCheck();
          }
          else{
            this.isPostFound = false;
            this.cdr.markForCheck();
          }
        }
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.announcements = this.announcementService.getAnnouncements()
      this.fetchAllPosts();
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
