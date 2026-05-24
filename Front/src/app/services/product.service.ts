import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, ProductPage, ProductStats } from '../models/product-model';

export interface ProductRequest {
  name: string;
  description?: string;
  price: number;
  imageUrl?: string;
  stock?: number;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly API = 'http://localhost:8080/api/v1/products';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 12): Observable<ProductPage> {
    return this.http.get<ProductPage>(`${this.API}?page=${page}&size=${size}`);
  }

  search(
    name?: string,
    minPrice?: number | null,
    maxPrice?: number | null,
    inStockOnly = false,
    page = 0,
    size = 12
  ): Observable<ProductPage> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (name) params = params.set('name', name);
    if (minPrice != null) params = params.set('minPrice', minPrice);
    if (maxPrice != null) params = params.set('maxPrice', maxPrice);
    if (inStockOnly) params = params.set('inStockOnly', 'true');
    return this.http.get<ProductPage>(`${this.API}/search`, { params });
  }

  getStats(): Observable<ProductStats> {
    return this.http.get<ProductStats>(`${this.API}/stats`);
  }

  getById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.API}/${id}`);
  }

  create(req: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.API, req);
  }

  update(id: string, req: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.API}/${id}`, req);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
