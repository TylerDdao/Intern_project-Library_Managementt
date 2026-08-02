import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private baseUrl = `${environment.apiUrl}/comments`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getComments(postId: number, page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&postId=${postId}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  createComment(content: string, postId: number){
    return this.http.post(`${this.baseUrl}`,{content: content, postId: postId}, {headers: getAuthHeaders(this.platformId)})
  }

  deleteComment(id:number){

  }
}
