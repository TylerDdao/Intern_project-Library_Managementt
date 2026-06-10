import { ChangeDetectorRef, Component, Inject, PLATFORM_ID } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { TranslateModule } from '@ngx-translate/core';
import { SideBarQuery, SortSideBarComponent } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { PostCardComponent } from "../../components/post-card-component/post-card-component";
import { Post } from '../../models/post';
import { LanguageService } from '../../services/language-service/language-service';
import { PostsService } from '../../services/posts-service/posts-service';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-browse-posts',
  imports: [NavbarComponent, TranslateModule, SortSideBarComponent, PostCardComponent],
  templateUrl: './browse-posts.html',
  styleUrl: './browse-posts.css',
})
export class BrowsePosts {
  isSearch:boolean = false;

  posts:Post[] =[];

  constructor(
    public langService: LanguageService,
    private postsService: PostsService,
    protected router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private cdr: ChangeDetectorRef,
  ) 
  {}

  fetchAllPosts():void{
    this.postsService.getAllPost().subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == "200"){
          this.posts = data.data.content;
          this.cdr.detectChanges();
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }

  handleApply(query: SideBarQuery): void {
    alert("Search")
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchAllPosts();
    }
  }
}
