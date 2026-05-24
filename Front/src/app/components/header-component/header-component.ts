import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-header-component',
  imports: [LucideAngularModule, RouterLink],
  templateUrl: './header-component.html',
  styleUrl: './header-component.scss',
})
export class HeaderComponent implements OnInit, OnDestroy {
  isMenuOpen = false;
  isUserMenuOpen = false;
  isLoggedIn = false;
  isAdmin = false;
  cartCount = 0;

  private subs = new Subscription();

  constructor(private auth: AuthService, private cart: CartService) {}

  ngOnInit() {
    this.subs.add(
      this.auth.isLoggedIn$.subscribe(v => {
        this.isLoggedIn = v;
        this.isAdmin = this.auth.isAdmin();
      })
    );
    this.subs.add(
      this.cart.items.subscribe(items => {
        this.cartCount = items.reduce((s, i) => s + i.quantity, 0);
      })
    );
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    if (this.isMenuOpen) this.isUserMenuOpen = false;
  }

  toggleUserMenu() {
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  closeMenus() {
    this.isMenuOpen = false;
    this.isUserMenuOpen = false;
  }

  logout() {
    this.auth.logout();
    this.isUserMenuOpen = false;
  }

  get userEmail(): string | null {
    return this.auth.getEmail();
  }
}
