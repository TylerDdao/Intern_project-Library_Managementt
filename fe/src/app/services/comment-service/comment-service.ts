import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class CommentService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getComments(postId: number, page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/comments?page=${page}&limit=${limit}&postId=${postId}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}
