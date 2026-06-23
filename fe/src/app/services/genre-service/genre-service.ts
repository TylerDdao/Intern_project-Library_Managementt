import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class GenreService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllGenres( page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/genres?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}
