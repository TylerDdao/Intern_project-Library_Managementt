import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
    const router = inject(Router);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
        return true;
    }

    const token = localStorage.getItem('token');

    if (!token) {
      localStorage.clear()
      router.navigate(['/login']);
      return false;
    }

    return http.get('http://localhost:8080/api/auth/check', {
        headers: { Authorization: `Bearer ${token}` }
    }).pipe(
        map(() => true),
        catchError((err) => {
            if (err.status === 401) {
                localStorage.clear();
                router.navigate(['/login']);
            }
            return of(false);
        })
    );
};

export const adminGuard: CanActivateFn = () => {
    const router = inject(Router);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }

    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user') ?? '{}');

    if (!token || user.role == "ROLE_USER") {
      localStorage.clear()
      router.navigate(['/login']);
      return false;
    }

    return http.get('http://localhost:8080/api/auth/check', {
        headers: { Authorization: `Bearer ${token}` }
    }).pipe(
        map(() => true),
        catchError((err) => {
            if (err.status === 401) {
                localStorage.clear();
                router.navigate(['/login']);
            }
            return of(false);
        })
    );
};