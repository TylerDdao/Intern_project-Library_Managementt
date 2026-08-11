import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { getAuthHeaders } from '../auth-service';
import { Post } from '../../models/post';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private baseUrl = `${environment.apiUrl}/posts`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  updatePost(post:Post){
    const body = {
      "id": post.id,
      "subject": post.subject,
      "content": post.content,
      "book": post.book.id
    }
    return this.http.patch(`${this.baseUrl}`, body, {headers:getAuthHeaders(this.platformId)})
  }

  getPostById(postId:number){
    return this.http.get(`${this.baseUrl}/${postId}`,{
      headers: getAuthHeaders(this.platformId)
    })
  }

  getMostLikesPosts(page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&sortBy=likeCount&sortDir=desc`, {
      headers: getAuthHeaders(this.platformId)
    });
  } 

  getAllPost(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getPostsByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&searchQuery=${query.searchQuery}&filterBy=${query.filterBy}&sortBy=createdAt`,{
        headers: getAuthHeaders(this.platformId)
      });
  }

  getMyPosts(page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}/my-posts?page=${page}&limit=${limit}`,{
        headers: getAuthHeaders(this.platformId)
      });
  }

  toggleLike(postId: number) {
    return this.http.post(`${this.baseUrl}/${postId}/like`, {}, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getPostsByBookId(bookId: number, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&bookId=${bookId}`, {
      headers: getAuthHeaders(this.platformId)
    })
  }

  deletePost(post:Post){
    return this.http.delete(`${this.baseUrl}?id=${post.id}`, {headers: getAuthHeaders(this.platformId)})
  }

  createPost(post: Post){
    const body = {
      "subject": post.subject,
      "content": post.content,
      "book": post.book.id
    }
    return this.http.post(`${this.baseUrl}`, body, {headers: getAuthHeaders(this.platformId)})
  }
}