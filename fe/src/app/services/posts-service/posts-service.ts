import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';

@Injectable({
  providedIn: 'root',
})
export class PostsService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  

  protected getAuthHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
    
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('token');
      const lang = localStorage.getItem('lang') ?? 'en';
      
      if (token) {
        headers = headers.set('Authorization', `Bearer ${token}`);
      }
      headers = headers.set('Accept-Language', lang);
    }
    
    return headers;
  }

  getAllPost(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/posts?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }

  getPostsByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}/posts?page=${page}&limit=${limit}&searchQuery=${query.searchQuery}&filterBy=${query.filterBy}&sortBy=createdAt`,{
        headers: this.getAuthHeaders()
      });
  }

  getMyPosts(userId: number, page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}/posts/my-posts?page=${page}&limit=${limit}&userId=${userId}`,{
        headers: this.getAuthHeaders()
      });
  }

  toggleLike(postId: number) {
    return this.http.post(`${this.baseUrl}/post/${postId}/like`, {}, {
      headers: this.getAuthHeaders()
    });
  }
}