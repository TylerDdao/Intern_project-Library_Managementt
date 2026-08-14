import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { Announcement } from '../../models/announcement';
import { environment } from '../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { getAuthHeaders } from '../auth-service';
import { errorNoti } from '../../util/error-notification';

import {
  getAnnouncementsFromStorage,
  saveAnnouncements,
  clearAnnouncements as clearAnnouncementsStorage
} from '../../util/session-storage';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService {

  private baseUrl = `${environment.apiUrl}/announcements`;

  private announcementsSubject =
  new BehaviorSubject<Announcement[]>([]);

  announcements$ = this.announcementsSubject.asObservable();

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService
  ) {
    if (isPlatformBrowser(this.platformId)) {
      this.announcementsSubject.next(
        getAnnouncementsFromStorage()
      );
    }
  }

  createAnnouncement(announcement:Announcement){
    const body = {
      type: announcement.type.toUpperCase(),
      subjectVi: announcement.subjectVi,
      contentVi: announcement.contentVi,
      subjectEn: announcement.subjectEn,
      contentEn: announcement.contentEn,
      subjectFr: announcement.subjectFr,
      contentFr: announcement.contentFr,
      link: announcement.link,
      linkTextVi: announcement.linkTextVi,
      linkTextEn: announcement.linkTextEn,
      linkTextFr: announcement.linkTextFr,
      isActive: announcement.active,
      locations: announcement.locations
    };
    return this.http.post(`${this.baseUrl}`, body, {headers:getAuthHeaders(this.platformId)}).pipe(
    tap(() => {
      sessionStorage.removeItem('announcementsLoaded');
    })
  );
  }

  updateAnnouncement(announcement: Announcement){
    const body ={
      id: announcement.id,
      isActive: announcement.active
    }
    return this.http.patch(`${this.baseUrl}?id=${announcement.id}`, body, {headers: getAuthHeaders(this.platformId)})
  }

  deleteAnnouncement(announcement: Announcement){
    return this.http.delete(`${this.baseUrl}?id=${announcement.id}`, {headers: getAuthHeaders(this.platformId)})
  }

  fetchAnnouncements(page = 0, limit = 10) {
    return this.http.get<any>(
      `${this.baseUrl}?page=${page}&number=${limit}`,
      {
        headers: getAuthHeaders(this.platformId)
      }
    );
  }

  // loadAnnouncements(): Observable<Announcement[]> {
  //   if (!isPlatformBrowser(this.platformId)) {
  //     return of([]);
  //   }

  //   const loaded = sessionStorage.getItem('announcementsLoaded');

  //   // Already loaded
  //   if (loaded === 'true') {
  //     const announcements = getAnnouncementsFromStorage();

  //     this.announcementsSubject.next(announcements);

  //     return of(announcements);
  //   }

  //   // First load
  //   return this.fetchAnnouncements().pipe(
  //     switchMap((response: any) => {
  //       if (response.code !== '200') {
  //         return of([]);
  //       }

  //       const totalElements = response.data.totalElements;

  //       return this.fetchAnnouncements(0, totalElements);
  //     }),

  //     map((response: any) => {
  //       if (response.code !== '200') {
  //         return [];
  //       }

  //       const announcements: Announcement[] =
  //         response.data.content;

  //       saveAnnouncements(announcements);

  //       sessionStorage.setItem(
  //         'announcementsLoaded',
  //         'true'
  //       );
  //       this.announcementsSubject.next(announcements);
  //       return announcements;
  //     }),

  //     catchError((err: HttpErrorResponse) => {
  //       errorNoti(err, this.translate);
  //       return of([]);
  //     })
  //   );
  // }

  loadAnnouncements(forceReload = false): Observable<Announcement[]> {
    if (!isPlatformBrowser(this.platformId)) {
      return of([]);
    }

    const loaded = sessionStorage.getItem('announcementsLoaded');

    if (!forceReload && loaded === 'true') {
      return of(getAnnouncementsFromStorage());
    }

    return this.fetchAnnouncements().pipe(
      switchMap((response: any) => {
        if (response.code !== '200') {
          return of([]);
        }

        const totalElements = response.data.totalElements;

        return this.fetchAnnouncements(0, totalElements);
      }),

      map((response: any) => {
        if (response.code !== '200') {
          return [];
        }

        const announcements: Announcement[] = response.data.content;

        saveAnnouncements(announcements);
        sessionStorage.setItem('announcementsLoaded', 'true');

        return announcements;
      }),

      catchError((err: HttpErrorResponse) => {
        errorNoti(err, this.translate);
        return of([]);
      })
    );
  }

  // loadAnnouncements(): void {
  //   if (!isPlatformBrowser(this.platformId)) {
  //     return;
  //   }

  //   this.fetchAnnouncements().pipe(
  //     switchMap((response: any) => {

  //       if (response.code !== '200') {
  //         return of([]);
  //       }

  //       const totalElements = response.data.totalElements;

  //       return this.fetchAnnouncements(0, totalElements);
  //     })
  //   ).subscribe({
  //     next: (response: any) => {
  //       if (response.code === '200') {
  //         saveAnnouncements(response.data.content);
  //       }
  //     },

  //     error: (err: HttpErrorResponse) => {
  //       errorNoti(err, this.translate);
  //     }
  //   });
  // }

  getAnnouncements(): Announcement[] {
    return getAnnouncementsFromStorage();
  }

  clearAnnouncements(): void {
    clearAnnouncementsStorage();
  }

  closeAnnouncement(id: number): void {
    const announcements = getAnnouncementsFromStorage();

    const announcement = announcements.find(
      a => a.id === id
    );

    if (announcement) {
      announcement.active = false;
      saveAnnouncements(announcements);
    }
  }
}