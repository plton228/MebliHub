export interface Order {
  id: string;
  userId: string;
  productId: string;
  productName: string;
  quantity: number;
  totalPrice: number;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  createdAt: string;
}

export interface OrderRequest {
  productId: string;
  quantity: number;
}

export interface OrderStats {
  totalOrders: number;
  totalRevenue: number;
  confirmedCount: number;
  pendingCount: number;
  cancelledCount: number;
}

export interface RevenueByProduct {
  productId: string;
  productName: string;
  orderCount: number;
  totalRevenue: number;
}
