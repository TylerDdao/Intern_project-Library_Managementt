import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class BorrowService {
  private baseUrl = `${environment.apiUrl}/borrows`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getBorrowsByUserId(userId: number | null = null, page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&userId=${userId}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBorrowByStatus(status: string, page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/${status}?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBorrowsCountByGenre() {
    return this.http.get(`${this.baseUrl}/borrows-count/genre`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}
