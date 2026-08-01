import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { SideBarQuery } from '../../components/sort-side-bar-component/sort-side-bar-component';
import { getAuthHeaders } from '../auth-service';
import { Policy } from '../../models/policy';

@Injectable({
  providedIn: 'root',
})
export class PolicyService {
  private baseUrl = `${environment.apiUrl}/policies`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getPolicyByKey(key:String){
    return this.http.get(`${this.baseUrl}?key=${key}`, 
      {headers: getAuthHeaders(this.platformId)}
    )
  }

  updatePolicy(policy: Policy){
    return this.http.patch(`${this.baseUrl}`, {key: policy.key, value: policy.value}, {headers:getAuthHeaders(this.platformId)});
  }
}