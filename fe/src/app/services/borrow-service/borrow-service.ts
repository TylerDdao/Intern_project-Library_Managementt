import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { Borrow } from '../../models/borrow';
import { Book } from '../../models/book';
import { User } from '../../models/user';
import { getUser } from '../../util/session-storage';

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

  getMyBorrows(isActive: boolean, bookId: number | null = null, page: number = 0, limit: number = 10) {
    if(bookId){
      return this.http.get(`${this.baseUrl}/my-borrows?isActive=${isActive}&bookId=${bookId}&page=${page}&limit=${limit}`, {
        headers: getAuthHeaders(this.platformId)
      });
    }
    else{
      return this.http.get(`${this.baseUrl}/my-borrows?isActive=${isActive}&page=${page}&limit=${limit}`, {
        headers: getAuthHeaders(this.platformId)
      });
    }
  }

  getBorrowsCountByGenre() {
    return this.http.get(`${this.baseUrl}/borrows-count/genre`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getAllActiveBorrows(page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getBorrowsByQuery(query: SideBarQuery, page: number = 0, limit: number = 10){
    return this.http.get(`${this.baseUrl}?query=${query.searchQuery}&sortBy=${query.sortBy.toLowerCase()}&page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    })
  }

  updateBorrow(borrow: Borrow) {
    return this.http.patch(`${this.baseUrl}/${borrow.id}`, 
      { id: borrow.id, isActive: false, dueDate: borrow.dueDate },
      { headers: getAuthHeaders(this.platformId) }
    );
  }

  returnBorrow(borrow: Borrow){
    return this.http.patch(`${this.baseUrl}/return/${borrow.id}`, 
      { id: borrow.id, isActive: false, dueDate: borrow.dueDate },
      { headers: getAuthHeaders(this.platformId) }
    );
  }

  createBorrow(book: Book, dueDate: String) {
    const user = getUser();
    return this.http.post(`${this.baseUrl}`,
      {bookId: book.id, userId: user?.id, dueDate: dueDate},
      {headers: getAuthHeaders(this.platformId)}
    )
  }
}
