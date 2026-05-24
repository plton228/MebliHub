export interface Product {
  id: string;
  name: string;
  price: number;
  description?: string;
  imageUrl?: string;
  stock?: number;
}

export interface ProductPage {
  content: Product[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export interface ProductStats {
  totalCount: number;
  minPrice: number;
  maxPrice: number;
  avgPrice: number;
  totalStock: number;
}
