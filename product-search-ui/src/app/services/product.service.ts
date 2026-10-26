import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse, Product } from './product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  // Relative URL: in development `ng serve` forwards /products to the backend (proxy.conf.json)
  private apiUrl = '/products';
  constructor(private http: HttpClient) {}

  searchProducts(
    name?: string,
    category?: string,
    maxPrice?: number | null,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<Product>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (name?.trim()) params = params.set('name', name.trim());
    if (category) params = params.set('category', category);
    // 0 is a valid max price: check for null/undefined, not for truthiness
    if (maxPrice != null) params = params.set('maxPrice', maxPrice);
    return this.http.get<PageResponse<Product>>(`${this.apiUrl}/search`, { params });
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }
}
