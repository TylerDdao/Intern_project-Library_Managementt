import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { getAuthHeaders } from '../auth-service';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllRoles(page:number = 0, limit:number=1000){
    return this.http.get(`${this.baseUrl}/roles?page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getRole(name:string, page: number = 0, limit:number=1000){
    return this.http.get(`${this.baseUrl}/roles?name=${name}&page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getUsersByRole(role: String, page: number = 0, limit:number = 10) {
    return this.http.get(`${this.baseUrl}/users?page=${page}&limit=${limit}&role=${role}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}
