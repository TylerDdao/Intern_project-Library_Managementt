import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class GenreService {
  private baseUrl = 'http://localhost:8080/api';

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

  getAllGenres( page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/genres?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }
}
