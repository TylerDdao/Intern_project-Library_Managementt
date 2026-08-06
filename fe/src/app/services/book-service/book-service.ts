import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';
import { Book } from '../../models/book';

@Injectable({
  providedIn: 'root',
})
export class BookService {
  private baseUrl = `${environment.apiUrl}/books`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  deleteBook(book:Book){
    return this.http.delete(`${this.baseUrl}?id=${book.id}`, {headers:getAuthHeaders(this.platformId)})
  }

  createBook(bookData: any, file: File | null) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(bookData)], { type: 'application/json' }));
    if (file) {
      formData.append('file', file);
    }

    return this.http.post(`${this.baseUrl}`, formData, { headers: getAuthHeaders(this.platformId) });
  }

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

  getBookById(id: number, title: string | null = null){
    return this.http.get(`${this.baseUrl}/book?id=${id}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  searchBook(query:string, page: number = 0, limit:number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&searchQuery=${query}`,{
      headers: getAuthHeaders(this.platformId)
    });
  }

  updateBook(book:Book){
    const body={
      id: book.id,
      title: book.title,
      copies: book.copies,
      genres: book.genres?.map(g=>g.name),
      author: book.author
    }
    return this.http.patch(`${this.baseUrl}`, body, {headers:getAuthHeaders(this.platformId)})
  }
}

