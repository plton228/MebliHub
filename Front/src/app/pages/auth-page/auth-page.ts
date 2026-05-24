import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './auth-page.html',
  styleUrl: './auth-page.scss',
})
export class AuthPage {
  tab: 'login' | 'register' = 'login';
  error = '';
  loading = false;

  login = { email: '', password: '' };
  reg = { name: '', lastName: '', email: '', password: '' };

  constructor(private auth: AuthService, private router: Router) {}

  switchTab(t: 'login' | 'register') {
    this.tab = t;
    this.error = '';
  }

  onLogin() {
    this.error = '';
    this.loading = true;
    this.auth.login(this.login).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => {
        this.error = err.error?.error ?? 'Невірний email або пароль';
        this.loading = false;
      }
    });
  }

  onRegister() {
    this.error = '';
    this.loading = true;
    this.auth.register(this.reg).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => {
        this.error = err.error?.error ?? 'Помилка реєстрації';
        this.loading = false;
      }
    });
  }
}
