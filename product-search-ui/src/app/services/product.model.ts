export interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  description: string;
  image?: string;
}

/** A page of results, as returned by the backend (PageResponse.java). */
export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
}
