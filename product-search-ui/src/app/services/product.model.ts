export interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  description: string;
  image?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}
