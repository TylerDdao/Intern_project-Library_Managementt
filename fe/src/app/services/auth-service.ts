import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string){
    const body = {
      'username': username,
      'password': password
    }
    return this.http.post(`${this.baseUrl}/login`, body);
  }

  // GET
  getBooks() {
    return this.http.get(`${this.baseUrl}/books`);
  }

  // GET by ID
  getBook(id: number) {
    return this.http.get(`${this.baseUrl}/books/${id}`);
  }

  // POST
  createBook(book: any) {
    return this.http.post(`${this.baseUrl}/books`, book);
  }

  // PUT
  updateBook(id: number, book: any) {
    return this.http.put(`${this.baseUrl}/books/${id}`, book);
  }

  // DELETE
  deleteBook(id: number) {
    return this.http.delete(`${this.baseUrl}/books/${id}`);
  }
}