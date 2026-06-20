import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class PostsService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllPost(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/posts?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getPostsByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}/posts?page=${page}&limit=${limit}&searchQuery=${query.searchQuery}&filterBy=${query.filterBy}&sortBy=createdAt`,{
        headers: getAuthHeaders(this.platformId)
      });
  }

  getMyPosts(userId: number, page: number = 0, limit: number = 10){
      return this.http.get(`${this.baseUrl}/posts/my-posts?page=${page}&limit=${limit}&userId=${userId}`,{
        headers: getAuthHeaders(this.platformId)
      });
  }

  toggleLike(postId: number) {
    return this.http.post(`${this.baseUrl}/post/${postId}/like`, {}, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}