import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { getAuthHeaders } from '../auth-service';
import { Role } from '../../models/role';
import { Feature } from '../../models/feature';

@Injectable({
  providedIn: 'root',
})
export class RoleService {

  private baseUrl = `${environment.apiUrl}/roles`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  getAllRoles(page:number = 0, limit:number=1000){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getRole(name:string, page: number = 0, limit:number=1000){
    return this.http.get(`${this.baseUrl}?name=${name}&page=${page}&limit=${limit}&sortBy=name&sortDir=asc`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  createRole(role: Role){
    const body = {
      "name": role.name,
      "default": role.default
    }
    return this.http.post(`${this.baseUrl}`,body, {headers: getAuthHeaders(this.platformId)})
  }

  updateRole(role: Role){
    const body = {
      "id": role.id,
      "name": role.name,
      "default": role.default
    }
    return this.http.patch(`${this.baseUrl}`, body, {headers: getAuthHeaders(this.platformId)});
  }

  assignFeature(role: Role, features: Feature[]){
    const body ={
      "features": features.map(feature => {return feature.name}),
      "id": role.id
    }
    return this.http.patch(`${this.baseUrl}/assign-feature`, body, {headers: getAuthHeaders(this.platformId)})
  }

  unassignFeature(role: Role, features: Feature[]){
    const body ={
      "features": features.map(feature => {return feature.name}),
      "id": role.id
    }
    return this.http.patch(`${this.baseUrl}/unassign-feature`, body, {headers: getAuthHeaders(this.platformId)})
  }

  deleteRole(role:Role){
    return this.http.delete(`${this.baseUrl}?id=${role.id}`, {headers:getAuthHeaders(this.platformId)})
  }

}
