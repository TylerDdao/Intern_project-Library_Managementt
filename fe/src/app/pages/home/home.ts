import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar";
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth-service';
import { LanguageService } from '../../services/language-service/language-service';
import { RouteReuseStrategy } from '@angular/router';
import { ChartComponent } from '../../components/chart-component/chart-component';
import { PostCardComponent } from '../../components/post-card-component/post-card-component';
import { BorrowCardComponent } from '../../components/borrow-card-component/borrow-card-component';
import { Post } from '../../models/post';
import { PostsService } from '../../services/posts-service/posts-service';

@Component({
  selector: 'app-home',
  imports: [TranslateModule, NavbarComponent, ChartComponent, PostCardComponent, BorrowCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  dueDate1: Date = new Date('2026-07-01');
  dueDate2: Date = new Date('2026-06-01');
  dueDate3: Date = new Date('2026-06-08');
  dueDate4: Date = new Date('2026-06-11');

  posts:Post[] = [];

  constructor(
    public langService: LanguageService,
    private postsService: PostsService,
    protected router: RouteReuseStrategy) 
  {}

  async fetchAllPosts(){
    this.postsService.getAllPost().subscribe({
      next: (data:any) => {
        console.log(data)
        if(data.code == 200){
          this.posts = data.content;
        }
      },
      error: (err) => {
        console.error(err);
      }

    })
  }
}
