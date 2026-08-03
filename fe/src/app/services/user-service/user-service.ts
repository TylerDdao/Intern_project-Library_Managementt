import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { getAuthHeaders } from '../auth-service';
import { User } from '../../models/user';
import { BehaviorSubject } from 'rxjs';
import { getUser, saveUser } from '../../util/session-storage';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private userBaseUrl = `${environment.apiUrl}/users`;
  private roleBaseUrl = `${environment.apiUrl}/roles`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  private userSubject = new BehaviorSubject<User | null>(getUser());

  user$ = this.userSubject.asObservable();

  setCurrentUser(user: User) {
    if(isPlatformBrowser(this.platformId)){
      saveUser(user);
      this.userSubject.next(user);
    }
  }

  getCurrentUser() {
    return this.userSubject.value;
  }

  getAllRoles(page:number = 0, limit:number=1000){
    return this.http.get(`${this.roleBaseUrl}?page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getRole(name:string, page: number = 0, limit:number=1000){
    return this.http.get(`${this.roleBaseUrl}?name=${name}&page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getUsersByRole(role: String, page: number = 0, limit:number = 10) {
    return this.http.get(`${this.userBaseUrl}?page=${page}&limit=${limit}&role=${role}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  checkUsernameAvailability(username: String){
    return this.http.get(`${this.userBaseUrl}/check-username?username=${username}`,
      {headers: getAuthHeaders(this.platformId)
      });
  }

  updateUser(user:User){
    return this.http.patch(`${this.userBaseUrl}`,
      {id: user.id, username: user.username, fullName: user.fullName, email: user.email, phoneNumber: user.phoneNumber, address: user.address},
      {headers: getAuthHeaders(this.platformId)})
  }

  updateUserRole(user:User){
    return this.http.patch(`${this.userBaseUrl}/update-role`,
      { id: user.id, role: user.role?.id },
      {headers: getAuthHeaders(this.platformId)})
  }
}
