import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BrowseBooks } from './pages/browse-books/browse-books';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';
import { Signup } from './pages/signup/signup';
import { ForgotPassword } from './pages/forgot-password/forgot-password';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'signup', component: Signup },
    { path: 'forgot-password', component: ForgotPassword},

    { path: 'home', component: Home, canActivate: [authGuard] },
    { path: 'books', component: BrowseBooks, canActivate: [authGuard] },
];
