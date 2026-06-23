import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private baseUrl = `${environment.apiUrl}/books`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getMostPostsBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/most-posts?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getMostBorrowedBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/most-borrowed?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getNewlyArrivedBooks(page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}/newly-arrived?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBooksByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&searchQuery=${query.searchQuery}&filterBy=${query.filterBy}&sortBy=${query.sortBy.toLowerCase()}`,{
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBooksCountByGenre(){
    return this.http.get(`${this.baseUrl}/books-count/genre`,{
      headers: getAuthHeaders(this.platformId)
    });
  }

  getUnavailableBooks(page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}/unavailable?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBook(id: number, title: string | null = null){
    return this.http.get(`${this.baseUrl}/book?id=${id}&title=${title}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}

