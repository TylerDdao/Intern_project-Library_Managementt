import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BrowseBooks } from './pages/browse-books/browse-books';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login },

    { path: 'home', component: Home, canActivate: [authGuard] },
    { path: 'books', component: BrowseBooks, canActivate: [authGuard] },
];
