import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { BrowseBooks } from './pages/browse-books/browse-books';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';
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
import { SignupSuccess } from './pages/signup-success/signup-success';
import { ChangePassword } from './pages/change-password/change-password';
import { ResetPassword } from './pages/reset-password/reset-password';

const admidFeatures = [
    "CREATE_BOOK", "CREATE_USER", "CREATE_ROLE", "CREATE_GENRE", 
    "GET_BORROW_MULTI",
    "UPDATE_BOOK", "UPDATE_USER_MULTI", "UPDATE_USER_ROLE", "UPDATE_BORROW", "UPDATE_ROLE"]

export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'signup', component: Signup },
    { path: 'signup/success', component: SignupSuccess },
    { path: 'forgot-password', component: ForgotPassword },
    { path: 'reset-password/:code/:email', component: ResetPassword},

    { path: 'home', component: Home, canActivate: [authGuard()]},

    { path: 'books', component: BrowseBooks, canActivate: [authGuard(["GET_BOOK"])] },
    { path: 'books/:book-id', component: BookPage, canActivate: [authGuard(["GET_BOOK", "CREATE_BORROW"])] },

    { path: 'posts', component: BrowsePosts, canActivate: [authGuard(["GET_POST"])] },
    { path: 'my-posts/:post-id', component: PostPage, canActivate: [authGuard(["GET_POST", "UPDATE_POST", "DELETE_POST"])]},
    {path: 'create-post', component: CreatePost, canActivate: [authGuard(["CREATE_POST"])]},

    { path: 'my-borrows', component: MyBorrows, canActivate: [authGuard(["GET_BORROW"])]},
    { path: 'my-posts', component: MyPosts, canActivate: [authGuard(["GET_POST"])]},
    { path: 'settings', component: Settings, canActivate: [authGuard(["UPDATE_USER"])]},
    { path: 'settings/change-password', component: ChangePassword, canActivate: [authGuard(["UPDATE_USER"])]},

    { path: 'dashboard', component: Dashboard, canActivate: [authGuard()]},
    { path: 'books-management', component: BooksManagement, canActivate: [authGuard()]},
    { path: 'borrows-management', component: BorrowsManagement, canActivate: [authGuard()]},
    { path: 'users-management', component: UsersManagement, canActivate: [authGuard()]},
    // { path: ''}

    {path: 'test', component: TestPage},

    { path: '**', component: NotFound },
];
