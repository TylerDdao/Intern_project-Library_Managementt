import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(username: string, password: string){
    const body = {
      'username': username,
      'password': password
    }
    return this.http.post(`${this.baseUrl}/login`, body);
  }

  signup(username: string, password:string, email:string, fullName:string, phoneNumber:string, province: string, city:string, addressLine1: string, addressLine2: string){
    const body = {
      'username': username,
      'password': password,
      'email': email,
      'fullName': fullName,
      'phoneNumber': phoneNumber,
      'province': province,
      'city': city, 
      'addressLine1': addressLine1, 
      'addressLine2': addressLine2
    }

    return this.http.post(`${this.baseUrl}/register`, body)
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