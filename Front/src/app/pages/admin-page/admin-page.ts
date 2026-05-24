import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { forkJoin } from 'rxjs';
import { ProductService, ProductRequest } from '../../services/product.service';
import { OrderService } from '../../services/order.service';
import { Product, ProductStats } from '../../models/product-model';
import { OrderStats, RevenueByProduct } from '../../models/order.model';

interface ProductForm {
  name: string;
  description: string;
  price: number | null;
  imageUrl: string;
  stock: number | null;
}

@Component({
  selector: 'app-admin-page',
  imports: [FormsModule, LucideAngularModule, RouterLink],
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.scss',
})
export class AdminPage implements OnInit {
  // CRUD
  products: Product[] = [];
  loading = true;
  error = '';
  successMsg = '';

  showForm = false;
  editingId: string | null = null;
  formError = '';
  form: ProductForm = this.emptyForm();

  // Stats
  productStats: ProductStats | null = null;
  orderStats: OrderStats | null = null;
  revenueByProduct: RevenueByProduct[] = [];
  statsLoading = true;

  constructor(
    private productService: ProductService,
    private orderService: OrderService
  ) {}

  ngOnInit() {
    this.loadProducts();
    this.loadStats();
  }

  loadStats() {
    this.statsLoading = true;
    forkJoin({
      products: this.productService.getStats(),
      orders: this.orderService.getOrderStats(),
      revenue: this.orderService.getRevenueByProduct()
    }).subscribe({
      next: ({ products, orders, revenue }) => {
        this.productStats = products;
        this.orderStats = orders;
        this.revenueByProduct = revenue;
        this.statsLoading = false;
      },
      error: () => { this.statsLoading = false; }
    });
  }

  loadProducts() {
    this.loading = true;
    this.productService.getAll(0, 100).subscribe({
      next: p => { this.products = p.content; this.loading = false; },
      error: () => { this.error = 'Помилка завантаження'; this.loading = false; }
    });
  }

  openAdd() {
    this.form = this.emptyForm();
    this.editingId = null;
    this.formError = '';
    this.showForm = true;
  }

  openEdit(p: Product) {
    this.form = {
      name: p.name,
      description: p.description ?? '',
      price: p.price,
      imageUrl: p.imageUrl ?? '',
      stock: p.stock ?? null,
    };
    this.editingId = p.id;
    this.formError = '';
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.formError = '';
  }

  saveProduct() {
    if (!this.form.name || this.form.price === null) {
      this.formError = 'Назва та ціна є обов\'язковими';
      return;
    }
    const req: ProductRequest = {
      name: this.form.name,
      description: this.form.description || undefined,
      price: this.form.price!,
      imageUrl: this.form.imageUrl || undefined,
      stock: this.form.stock ?? undefined,
    };

    const call = this.editingId
      ? this.productService.update(this.editingId, req)
      : this.productService.create(req);

    call.subscribe({
      next: () => {
        this.showForm = false;
        this.flash(this.editingId ? 'Товар оновлено' : 'Товар додано');
        this.loadProducts();
        this.loadStats();
      },
      error: () => { this.formError = 'Помилка збереження'; }
    });
  }

  deleteProduct(p: Product) {
    if (!confirm(`Видалити "${p.name}"?`)) return;
    this.productService.delete(p.id).subscribe({
      next: () => {
        this.flash('Товар видалено');
        this.loadProducts();
        this.loadStats();
      },
      error: () => { this.error = 'Помилка видалення'; }
    });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('uk-UA', {
      style: 'currency',
      currency: 'UAH',
      maximumFractionDigits: 0
    }).format(price);
  }

  private emptyForm(): ProductForm {
    return { name: '', description: '', price: null, imageUrl: '', stock: null };
  }

  private flash(msg: string) {
    this.successMsg = msg;
    setTimeout(() => this.successMsg = '', 3000);
  }
}
