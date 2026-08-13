import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { TranslateService } from '@ngx-translate/core';
import { getAuthHeaders } from '../auth-service';
import { User } from '../../models/user';
import { Book } from '../../models/book';
import { Borrow } from '../../models/borrow';

@Injectable({
  providedIn: 'root',
})
export class ExportService {
  private baseUrl = `${environment.apiUrl}/export`;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ) {}

  private getTimeStamp():string{
    const now = new Date();
    const timestamp = now.toISOString().slice(0, 19).replace('T', '_').replace(/:/g, '-');
    return timestamp
  }

  private downloadBlob(response: HttpResponse<Blob>, filename: string) {
    const blob = response.body as Blob;
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  exportLogs(from: string, to: string) {
  const filename = from === to ? `logs-${from}.xlsx` : `logs-${from}_to_${to}.xlsx`;
  this.http
    .post(`${this.baseUrl}/log`, null, {
      params: { from, to },
      responseType: 'blob',
      observe: 'response',
      headers: getAuthHeaders(this.platformId),
    })
    .subscribe({
      next: (response) => this.downloadBlob(response, `logs-from${from}-${to}-.xlsx`),
      error: (err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
      },
    });
}

  exportBorrow(borrows: Borrow[]) {
    if (borrows.length > 0) {
      const payload = borrows.map(b => ({ id:b.id }));
      this.http
        .post(`${this.baseUrl}/borrows`, payload, {
          responseType: 'blob',
          observe: 'response',
          headers: getAuthHeaders(this.platformId),
        })
        .subscribe({
          next: (response) => this.downloadBlob(response, `borrows-export-${this.getTimeStamp()}.xlsx`),
          error: (err: HttpErrorResponse) => {
            errorNoti(err, this.translate);
          },
        });
    }
  }

  exportBook(books: Book[]) {
    if (books.length > 0) {
      const payload = books.map(b => ({ id:b.id }));
      this.http
        .post(`${this.baseUrl}/books`, payload, {
          responseType: 'blob',
          observe: 'response',
          headers: getAuthHeaders(this.platformId),
        })
        .subscribe({
          next: (response) => this.downloadBlob(response, `books-export-${this.getTimeStamp()}.xlsx`),
          error: (err: HttpErrorResponse) => {
            errorNoti(err, this.translate);
          },
        });
    }
  }

  exportUsers(users: User[]) {
    if (users.length > 0) {
      const payload = users.map(u => ({ id: u.id }));
      this.http
        .post(`${this.baseUrl}/users`, payload, {
          responseType: 'blob',
          observe: 'response',
          headers: getAuthHeaders(this.platformId),
        })
        .subscribe({
          next: (response) => this.downloadBlob(response, `users-export-${this.getTimeStamp()}.xlsx`),
          error: (err: HttpErrorResponse) => {
            errorNoti(err, this.translate);
          },
        });
    }
  }
}