export interface Product {
  _id: string; 
  name: string;
  price: number;
  description?: string;
  metadata: Record<string, any>; // Гнучкі характеристики мебелі
}
