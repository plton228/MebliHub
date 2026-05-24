import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Order } from '../../models/order.model';

type StatusTab = 'ALL' | 'CONFIRMED' | 'PENDING' | 'CANCELLED';

@Component({
  selector: 'app-account-pages',
  imports: [LucideAngularModule, RouterLink],
  templateUrl: './account-pages.html',
  styleUrl: './account-pages.scss',
})
export class AccountPages implements OnInit {
  orders: Order[] = [];
  loading = true;
  error = '';
  activeTab: StatusTab = 'ALL';

  readonly tabs: { key: StatusTab; label: string }[] = [
    { key: 'ALL',       label: 'Всі' },
    { key: 'CONFIRMED', label: 'Підтверджені' },
    { key: 'PENDING',   label: 'Очікують' },
    { key: 'CANCELLED', label: 'Скасовані' },
  ];

  constructor(private orderService: OrderService, private auth: AuthService) {}

  ngOnInit() {
    this.loadOrders();
  }

  setTab(tab: StatusTab) {
    if (this.activeTab === tab) return;
    this.activeTab = tab;
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.error = '';
    const obs = this.activeTab === 'ALL'
      ? this.orderService.getMyOrders()
      : this.orderService.getMyOrdersByStatus(this.activeTab);

    obs.subscribe({
      next: data => { this.orders = data; this.loading = false; },
      error: () => { this.error = 'Не вдалося завантажити замовлення'; this.loading = false; }
    });
  }

  get userEmail(): string | null {
    return this.auth.getEmail();
  }

  get isAdmin(): boolean {
    return this.auth.isAdmin();
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', {
      style: 'currency',
      currency: 'UAH',
      maximumFractionDigits: 0
    }).format(price);
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString('uk-UA', {
      day: '2-digit', month: 'long', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  statusLabel(status: string): string {
    const labels: Record<string, string> = {
      CONFIRMED: 'Підтверджено',
      PENDING: 'Очікує',
      CANCELLED: 'Скасовано'
    };
    return labels[status] ?? status;
  }
}
