import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { CartItem } from '../../models/product-model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-cart',
  imports: [LucideAngularModule, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.scss',
})
export class Cart implements OnInit {
  items: CartItem[] = [];
  loading = false;
  success = false;
  error = '';

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit() {
    this.cartService.items.subscribe(i => this.items = i);
  }

  get total(): number {
    return this.items.reduce((s, i) => s + i.product.price * i.quantity, 0);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH', maximumFractionDigits: 0 })
      .format(price);
  }

  increase(item: CartItem) {
    this.cartService.updateQuantity(item.product.id, item.quantity + 1);
  }

  decrease(item: CartItem) {
    this.cartService.updateQuantity(item.product.id, item.quantity - 1);
  }

  remove(item: CartItem) {
    this.cartService.removeItem(item.product.id);
  }

  checkout() {
    this.loading = true;
    this.error = '';
    const requests = this.items.map(i =>
      this.orderService.createOrder({ productId: i.product.id, quantity: i.quantity })
    );
    forkJoin(requests).subscribe({
      next: () => {
        this.cartService.clear();
        this.success = true;
        this.loading = false;
        setTimeout(() => this.router.navigate(['/account']), 2000);
      },
      error: () => {
        this.error = 'Помилка при оформленні замовлення';
        this.loading = false;
      }
    });
  }
}
