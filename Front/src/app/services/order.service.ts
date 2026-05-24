import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, OrderRequest, OrderStats, RevenueByProduct } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly API = 'http://localhost:8080/api/v1/orders';

  constructor(private http: HttpClient) {}

  createOrder(req: OrderRequest): Observable<Order> {
    return this.http.post<Order>(this.API, req);
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.API}/my`);
  }

  getMyOrdersByStatus(status: string): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.API}/my/status?status=${status}`);
  }

  getOrderStats(): Observable<OrderStats> {
    return this.http.get<OrderStats>(`${this.API}/stats`);
  }

  getRevenueByProduct(): Observable<RevenueByProduct[]> {
    return this.http.get<RevenueByProduct[]>(`${this.API}/revenue-by-product`);
  }
}
