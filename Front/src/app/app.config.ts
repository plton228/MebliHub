import { ApplicationConfig, provideBrowserGlobalErrorListeners, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { LucideAngularModule, Search, TextAlignJustify, ShoppingBasket, User, LogOut, Trash2, Pencil, Plus, X, ChevronLeft, ChevronRight, Package, ShoppingCart, Check } from 'lucide-angular';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideHttpClient(withInterceptors([authInterceptor]), withFetch()),
    importProvidersFrom(
      LucideAngularModule.pick({
        Search, TextAlignJustify, ShoppingBasket, User,
        LogOut, Trash2, Pencil, Plus, X,
        ChevronLeft, ChevronRight, Package, ShoppingCart, Check
      })
    )
  ]
};
