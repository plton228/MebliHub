import { Component, Input, Output, EventEmitter } from '@angular/core';
import { LucideAngularModule } from 'lucide-angular';
import { Product } from '../../models/product-model';

@Component({
  selector: 'app-product-card',
  imports: [LucideAngularModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.scss',
})
export class ProductCard {
  @Input() product!: Product;
  @Output() addToCart = new EventEmitter<Product>();

  get inStock(): boolean {
    return this.product.stock === undefined || this.product.stock > 0;
  }

  get formattedPrice(): string {
    return new Intl.NumberFormat('uk-UA', { style: 'currency', currency: 'UAH', maximumFractionDigits: 0 })
      .format(this.product.price);
  }

  onAddToCart() {
    this.addToCart.emit(this.product);
  }
}
