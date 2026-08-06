import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { getAuthHeaders } from '../auth-service';
import { Genre } from '../../models/genre';

@Injectable({
  providedIn: 'root',
})
export class GenreService {
  private baseUrl = `${environment.apiUrl}/genres`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  createGenre(genre:Genre){
    return this.http.post(`${this.baseUrl}`, 
      {name: genre.name},
      {headers: getAuthHeaders(this.platformId)}
    )
  }

  getAllGenres( page: number = 0, limit: number = 10) {
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  getGenresByName(name: string, page: number = 0, limit:number = 10){
    return this.http.get(`${this.baseUrl}?page=${page}&limit=${limit}&name=${name}`, {
      headers: getAuthHeaders(this.platformId)
    });
  }

  deleteGenre(genre:Genre){
    return this.http.delete(`${this.baseUrl}?id=${genre.id}`, {headers: getAuthHeaders(this.platformId)})
  }
}
