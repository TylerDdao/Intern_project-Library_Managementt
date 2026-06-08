import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class PostsService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}
  getAllPost(page:number = 0, limit: number = 10) {
    return this.http.get(`/posts?page=${page}&limit=${limit}`);
  }
}