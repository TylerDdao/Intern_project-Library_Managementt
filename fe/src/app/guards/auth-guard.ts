import { inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../environments/environment';
import { getUser } from '../util/session-storage';
import { UserService } from '../services/user-service/user-service';

const baseUrl = environment.apiUrl;

export function rootOnly(): CanActivateFn {
  return () => {
    const router = inject(Router);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }
    const user = getUser();
    if(user?.role?.name == "ROLE_ROOT"){
        return true;
    }
    return router.createUrlTree(['/unauthorzied']);
  };
}

export function onDev(): CanActivateFn {
  return () => {
    const router = inject(Router);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }
    const user = getUser();
    if(user?.role?.name == "ROLE_ROOT"){
        return true;
    }
    return router.createUrlTree(['/on-dev']);
  };
}

export function authGuard(
  acceptedFeatures: string[] = []
): CanActivateFn {

  return () => {

    const router = inject(Router);
    const userService = inject(UserService);
    const http = inject(HttpClient);
    const platformId = inject(PLATFORM_ID);

    if (!isPlatformBrowser(platformId)) {
      return true;
    }

    const token = sessionStorage.getItem('token');
    const user = getUser();

    // No authentication
    // User should go to LOGIN
    if (!token || !user) {
      sessionStorage.clear();
      return router.createUrlTree(['/login']);
    }
    userService.setCurrentUser(user);
    return http.get(`${baseUrl}/auth/check`, {
      headers: {Authorization: `Bearer ${token}`}
    }).pipe(
      map(() => {
        // ROOT can access everything
        if (user.role?.name === 'ROLE_ROOT') {
          return true;
        }

        // No feature restriction
        if (acceptedFeatures.length === 0) {
          return true;
        }

        // Get user's features
        const userFeatures =
          user.role?.features?.map(f => f.name) ?? [];

        // Check permission
        const hasAccess = acceptedFeatures.some(
          feature => userFeatures.includes(feature)
        );

        // User is logged in but does NOT have permission
        if (!hasAccess) {
          return router.createUrlTree(['/unauthorized']);
        }
        return true;
      }),
      catchError((err) => {
        
        // Token is invalid/expired
        if (err.status === 401) {
          sessionStorage.clear();
          return of(
            router.createUrlTree(['/unauthorized'])
          );
        }
        return of(false);
      })
    );
  };
}