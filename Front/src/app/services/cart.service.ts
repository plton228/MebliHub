import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CartItem, Product } from '../models/product-model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly CART_KEY = 'mh_cart';
  private items$ = new BehaviorSubject<CartItem[]>(this.loadCart());

  get items() {
    return this.items$.asObservable();
  }

  get count(): number {
    return this.items$.value.reduce((sum, i) => sum + i.quantity, 0);
  }

  get total(): number {
    return this.items$.value.reduce((sum, i) => sum + i.product.price * i.quantity, 0);
  }

  get snapshot(): CartItem[] {
    return this.items$.value;
  }

  addItem(product: Product, quantity = 1): void {
    const current = [...this.items$.value];
    const idx = current.findIndex(i => i.product.id === product.id);
    if (idx >= 0) {
      current[idx] = { ...current[idx], quantity: current[idx].quantity + quantity };
    } else {
      current.push({ product, quantity });
    }
    this.saveCart(current);
  }

  removeItem(productId: string): void {
    const current = this.items$.value.filter(i => i.product.id !== productId);
    this.saveCart(current);
  }

  updateQuantity(productId: string, quantity: number): void {
    if (quantity <= 0) {
      this.removeItem(productId);
      return;
    }
    const current = this.items$.value.map(i =>
      i.product.id === productId ? { ...i, quantity } : i
    );
    this.saveCart(current);
  }

  clear(): void {
    this.saveCart([]);
  }

  private saveCart(items: CartItem[]): void {
    localStorage.setItem(this.CART_KEY, JSON.stringify(items));
    this.items$.next(items);
  }

  private loadCart(): CartItem[] {
    try {
      return JSON.parse(localStorage.getItem(this.CART_KEY) ?? '[]');
    } catch {
      return [];
    }
  }
}
