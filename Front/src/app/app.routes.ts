import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/main-page/main-page').then(m => m.MainPage)
  },
  {
    path: 'auth',
    loadComponent: () => import('./pages/auth-page/auth-page').then(m => m.AuthPage)
  },
  {
    path: 'account',
    loadComponent: () => import('./pages/account-pages/account-pages').then(m => m.AccountPages),
    canActivate: [authGuard]
  },
  {
    path: 'cart',
    loadComponent: () => import('./pages/cart/cart').then(m => m.Cart),
    canActivate: [authGuard]
  },
  {
    path: 'admin',
    loadComponent: () => import('./pages/admin-page/admin-page').then(m => m.AdminPage),
    canActivate: [authGuard, adminGuard]
  },
  { path: '**', redirectTo: '' }
];
