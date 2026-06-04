import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = () => {
  // const router = inject(Router);
  // const http = inject(HttpClient);
  // const token = localStorage.getItem('token');

  // if (!token) {
  //   router.navigate(['/login']);
  //   return false;
  // }

  // return http.get('http://localhost:8080/api/auth/check', {
  //   headers: { Authorization: `Bearer ${token}` }
  // }).pipe(
  //   map(() => true),
  //   catchError((err) => {
  //     if (err.status === 401) {
  //       localStorage.clear();
  //       router.navigate(['/login']);
  //     }
  //     return of(false);
  //   })
  // );

  return true;
};