import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';
import { Product } from '../services/product.model';

@Component({
  selector: 'app-product-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-search.html',
  styleUrls: ['./product-search.css'],
})
export class ProductSearchComponent implements OnInit {
  products: Product[] = [];
  totalElements = 0;
  page = 0;
  size = 5;
  categories: string[] = [];

  // filters
  name = '';
  category = '';
  maxPrice?: number;

  constructor(public productService: ProductService) {}

  ngOnInit(): void {
    this.search();
    this.loadCategories();
  }

  loadCategories() {
    this.productService.getCategories().subscribe((response: string[]) => {
      this.categories = Array.isArray(response)
        ? Array.from(new Set(response)).sort((a, b) => a.localeCompare(b))
        : [];
    });
  }

  search(): void {
    this.productService
      .searchProducts(this.name, this.category, this.maxPrice, this.page, this.size)
      .subscribe((response) => {
        this.products = response.content;
        this.totalElements = response.totalElements;
      });
  }

  onPageChange(newPage: number): void {
    this.page = newPage;
    this.search();
  }

  clearFilters(): void {
    this.name = '';
    this.category = '';
    this.maxPrice = undefined;
    this.page = 0;
    this.search();
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
