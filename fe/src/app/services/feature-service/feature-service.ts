import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';
import { Role } from '../../models/role';
import { Feature } from '../../models/feature';

@Injectable({
  providedIn: 'root'
})
export class FeatureService {
  private baseUrl = `${environment.apiUrl}`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllFeatures(page: number = 0, limit:number = 100){
    return this.http.get(`${this.baseUrl}/features?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }
}
