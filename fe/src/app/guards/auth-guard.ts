import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../environments/environment';
import { getUser } from '../util/session-storage';

const baseUrl = environment.apiUrl;

export function authGuard(acceptedFeatures: string[] = []): CanActivateFn {
  return () => {
    const router = inject(Router);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }

    const token = sessionStorage.getItem('token');
    const user = getUser();

    if (!token || !user) {
      sessionStorage.clear();
      router.navigate(['/login']);
      return false;
    }

    return http.get(`${baseUrl}/auth/check`, {
      headers: { Authorization: `Bearer ${token}` }
    }).pipe(
      map(() => {
        if (user.role?.name === 'ROLE_ROOT') {
          return true;
        }

        if (acceptedFeatures.length === 0) {
            if(user){
                return true;
            }
            else{
                sessionStorage.clear();
                router.navigate(['/login']);
            }    
        }

        const userFeatures = user.role?.features?.map(f => f.name) ?? [];
        const hasAccess = acceptedFeatures.some(feature => userFeatures.includes(feature));

        if (!hasAccess) {
            sessionStorage.clear();
            router.navigate(['/login']);
        }

        return hasAccess;
      }),
      catchError((err) => {
        if (err.status === 401) {
          sessionStorage.clear();
          router.navigate(['/login']);
        }
        return of(false);
      })
    );
  };
}