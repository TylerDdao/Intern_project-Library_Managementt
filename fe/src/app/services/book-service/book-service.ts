import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class BookService {
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

  getAllBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/books?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }

  getMostPostsBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/books/most-posts?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }

  getMostBorrowedBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/books/most-borrowed?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }

  getNewlyArrivedBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/books/newly-arrived?page=${page}&limit=${limit}`, {
      headers: this.getAuthHeaders()
    });
  }

  getBooksByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}/books?page=${page}&limit=${limit}&searchQuery=${query.searchQuery}&filterBy=${query.filterBy}&sortBy=${query.sortBy}`,{
      headers: this.getAuthHeaders()
    })
  }

  getBooksByGenre(genre: string, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}/books/books-count?page=${page}&limit=${limit}&genre=${genre}`,{
      headers: this.getAuthHeaders()
    })
  }
// /books/books-count/borrowed
  getBorrowedBooksByGenre(genre: string, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}/books/books-count/borrowed?page=${page}&limit=${limit}&genre=${genre}`,{
      headers: this.getAuthHeaders()
    })
  }
}

