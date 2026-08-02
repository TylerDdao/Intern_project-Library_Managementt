import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BrowseBooks } from './pages/browse-books/browse-books';
import { Login } from './pages/login/login';
import { adminGuard, authGuard } from './guards/auth-guard';
import { Signup } from './pages/signup/signup';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { BrowsePosts } from './pages/browse-posts/browse-posts';
import { MyBorrows } from './pages/my-borrows/my-borrows';
import { MyPosts } from './pages/my-posts/my-posts';
import { Settings } from './pages/settings/settings';
import { NotFound } from './pages/not-found/not-found';
import { Dashboard } from './pages/dashboard/dashboard';
import { BooksManagement } from './pages/books-management/books-management';
import { BorrowsManagement } from './pages/borrows-management/borrows-management';
import { UsersManagement } from './pages/users-management/users-management';
import { BookPage } from './pages/book-page/book-page';
import { PostPage } from './pages/post-page/post-page';
import { CreatePost } from './pages/create-post/create-post';
import { TestPage } from './pages/test-page/test-page';

export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'signup', component: Signup },
    { path: 'forgot-password', component: ForgotPassword},

    { path: 'home', component: Home, canActivate: [authGuard] },

    { path: 'books', component: BrowseBooks, canActivate: [authGuard] },
    { path: 'books/:book-id', component: BookPage, canActivate: [authGuard] },

    { path: 'posts', component: BrowsePosts, canActivate: [authGuard] },
    { path: 'my-posts/:post-id', component: PostPage, canActivate: [authGuard]},

    { path: 'my-borrows', component: MyBorrows, canActivate: [authGuard]},
    { path: 'my-posts', component: MyPosts, canActivate: [authGuard]},
    { path: 'settings', component: Settings, canActivate: [authGuard]},

    {path: 'create-post', component: CreatePost, canActivate: [authGuard]},

    { path: 'dashboard', component: Dashboard, canActivate: [adminGuard]},
    { path: 'books-management', component: BooksManagement, canActivate: [adminGuard]},
    { path: 'borrows-management', component: BorrowsManagement, canActivate: [adminGuard]},
    { path: 'users-management', component: UsersManagement, canActivate: [adminGuard]},

    {path: 'test', component: TestPage},

    { path: '**', component: NotFound }
];
