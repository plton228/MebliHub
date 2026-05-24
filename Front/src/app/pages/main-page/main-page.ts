import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { HeroSection } from '../../components/hero-section/hero-section';
import { ProductCard } from '../../components/product-card/product-card';
import { Product, ProductPage } from '../../models/product-model';

@Component({
  selector: 'app-main-page',
  imports: [HeroSection, ProductCard, LucideAngularModule, FormsModule],
  templateUrl: './main-page.html',
  styleUrl: './main-page.scss',
})
export class MainPage implements OnInit {
  page: ProductPage | null = null;
  currentPage = 0;
  pageSize = 12;

  filterName = '';
  filterMinPrice: number | null = null;
  filterMaxPrice: number | null = null;
  filterInStockOnly = false;
  searchMode = false;

  error = '';

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    if (this.searchMode) {
      this.productService.search(
        this.filterName || undefined,
        this.filterMinPrice,
        this.filterMaxPrice,
        this.filterInStockOnly,
        this.currentPage,
        this.pageSize
      ).subscribe({
        next: data => { this.page = data; this.error = ''; },
        error: () => { this.error = 'Не вдалося виконати пошук'; }
      });
    } else {
      this.productService.getAll(this.currentPage, this.pageSize).subscribe({
        next: data => { this.page = data; this.error = ''; },
        error: () => { this.error = 'Не вдалося завантажити товари'; }
      });
    }
  }

  get hasActiveFilters(): boolean {
    return !!(
      this.filterName.trim() ||
      this.filterMinPrice !== null ||
      this.filterMaxPrice !== null ||
      this.filterInStockOnly
    );
  }

  get displayedProducts(): Product[] {
    return this.page?.content ?? [];
  }

  applySearch() {
    this.currentPage = 0;
    this.searchMode = this.hasActiveFilters;
    this.loadProducts();
  }

  resetFilters() {
    this.filterName = '';
    this.filterMinPrice = null;
    this.filterMaxPrice = null;
    this.filterInStockOnly = false;
    this.currentPage = 0;
    this.searchMode = false;
    this.loadProducts();
  }

  get totalPages(): number {
    return this.page?.totalPages ?? 0;
  }

  goToPage(p: number) {
    if (p < 0 || p >= this.totalPages) return;
    this.currentPage = p;
    this.loadProducts();
    document.getElementById('catalog')?.scrollIntoView({ behavior: 'smooth' });
  }

  addToCart(product: Product) {
    if (!this.authService.isLoggedIn) {
      this.router.navigate(['/auth']);
      return;
    }
    this.cartService.addItem(product);
  }

  get pages(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i);
  }
}
