import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { User } from '../models/user';
import { isPlatformBrowser } from '@angular/common';

export function getAuthHeaders(platformId: Object): HttpHeaders {
  let headers = new HttpHeaders();
  if (isPlatformBrowser(platformId)) {
    const token = sessionStorage.getItem('token');
    const lang = sessionStorage.getItem('lang') ?? 'en';
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    headers = headers.set('Accept-Language', lang);
  }
  return headers;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object) {}

  sendVerificationCode(email: string, fullName:string){
    const body = {
      "email": email,
      "fullName": fullName
    }
    return this.http.post(`${this.baseUrl}/send-verification-code`, body, {
      headers:getAuthHeaders(this.platformId)
    })
  }

  submitVerificationCode(email: string, code: string){
    const body = {
      "email": email,
      "code": code
    }

    return this.http.post(`${this.baseUrl}/verify`, body,{
      headers: getAuthHeaders(this.platformId)
    })
  }

  login(username: string, password: string){
    const body = {
      'username': username,
      'password': password
    }
    return this.http.post(`${this.baseUrl}/login`, body);
  }

  signup(user:User){
    const body = {
      'username': user.username,
      'password': user.password,
      'email': user.email,
      'fullName': user.fullName,
      'phoneNumber': user.phoneNumber,
      'address': user.address,
    }

    return this.http.post(`${this.baseUrl}/register`, body)
  }

  update(user: User){
    let address;
    const body = {
      'username': user.username,
      'email': user.email,
      'fullName': user.fullName,
      'phoneNumber': user.phoneNumber,
      'address': user.address
    }
  }

  logout(){
    return this.http.post(`${this.baseUrl}/logout`, {}, {
        headers: getAuthHeaders(this.platformId)
    });
  }
}