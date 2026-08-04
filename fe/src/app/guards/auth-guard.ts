import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
const baseUrl = environment.apiUrl


export const authGuard: CanActivateFn = () => {
    const router = inject(Router);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);
    const translate = inject(TranslateService);

    if (!isPlatformBrowser(platformId)) {
        return true;
    }

    const token = sessionStorage.getItem('token');

    if (!token) {
      sessionStorage.clear()
      router.navigate(['/login']);
      return false;
    }

    return http.get(`${baseUrl}/auth/check`, {
        headers: { Authorization: `Bearer ${token}` }
    }).pipe(
        map(() => true),
        catchError((err) => {
            if (err.status === 401) {
                const message = translate.instant("error.Unauthorized-access")
                alert(message)
                sessionStorage.clear();
                router.navigate(['/login']);
            }
            return of(false);
        })
    );
};

export const adminGuard: CanActivateFn = () => {
    
    const translate = inject(TranslateService); 
    const router = inject(Router);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }

    const token = sessionStorage.getItem('token');
    const user = JSON.parse(sessionStorage.getItem('user') ?? '{}');

    if (!token || user.role == "ROLE_USER") {
      sessionStorage.clear()
      router.navigate(['/login']);
      return false;
    }

    return http.get(`${baseUrl}/auth/check`, {
        headers: { Authorization: `Bearer ${token}` }
    }).pipe(
        map(() => true),
        catchError((err) => {
            if (err.status === 401) {
                const message = translate.instant("error.Unauthorized-access")
                alert(message)
                sessionStorage.clear();
                router.navigate(['/login']);
            }
            return of(false);
        })
    );
};