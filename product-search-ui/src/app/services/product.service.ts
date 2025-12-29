import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse, Product } from './product.model';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/products';
  constructor(private http: HttpClient) {}

  searchProducts(
    name?: string,
    category?: string,
    maxPrice?: number,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<Product>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (name) params = params.set('name', name);
    if (category) params = params.set('category', category);
    if (maxPrice) params = params.set('maxPrice', maxPrice);
    return this.http.get<PageResponse<Product>>(`${this.apiUrl}/search`, { params });
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/categories`);
  }
}
