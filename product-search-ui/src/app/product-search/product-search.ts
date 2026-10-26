import { Component, OnDestroy, OnInit } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ProductService } from '../services/product.service';
import { Product } from '../services/product.model';

@Component({
  selector: 'app-product-search',
  standalone: true,
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './product-search.html',
  styleUrls: ['./product-search.css'],
})
export class ProductSearchComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  totalElements = 0;
  page = 0;
  size = 5;
  categories: string[] = [];
  loading = false;
  error = '';

  // filters
  name = '';
  category = '';
  maxPrice: number | null = null;

  private request?: Subscription;

  constructor(public productService: ProductService) {}

  ngOnInit(): void {
    this.load();
    this.loadCategories();
  }

  ngOnDestroy(): void {
    this.request?.unsubscribe();
  }

  loadCategories() {
    this.productService.getCategories().subscribe({
      next: (response: string[]) => {
        this.categories = Array.isArray(response)
          ? Array.from(new Set(response)).sort((a, b) => a.localeCompare(b))
          : [];
      },
      error: () => (this.categories = []),
    });
  }

  /** A new search always starts again from the first page. */
  search(): void {
    this.page = 0;
    this.load();
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.load();
  }

  clearFilters(): void {
    this.name = '';
    this.category = '';
    this.maxPrice = null;
    this.search();
  }

  private load(): void {
    // A newer search makes the answer of the previous one useless: cancel it
    this.request?.unsubscribe();
    this.loading = true;
    this.error = '';
    this.request = this.productService
      .searchProducts(this.name, this.category, this.maxPrice, this.page, this.size)
      .subscribe({
        next: (response) => {
          this.products = response.content;
          this.totalElements = response.totalElements;
          this.loading = false;
        },
        error: (err: HttpErrorResponse) => {
          this.products = [];
          this.totalElements = 0;
          this.loading = false;
          this.error =
            err.status === 0
              ? 'Cannot reach the server. Is the backend running on port 8080?'
              : err.error?.detail ?? `Search failed (HTTP ${err.status}).`;
        },
      });
  }

  onImgError(event: Event): void {
    const img = event.target as HTMLImageElement;
    if (img) {
      // small transparent SVG placeholder
      img.src =
        'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="200" height="140"><rect width="100%" height="100%" fill="%23eee"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-family="Arial" font-size="14">No image</text></svg>';
    }
  }

  get totalPages(): number {
    return Math.ceil(this.totalElements / this.size) || 1;
  }
}
