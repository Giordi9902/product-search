import { Component } from '@angular/core';
import { ProductSearchComponent } from './product-search/product-search';

@Component({
  selector: 'app-root',
  imports: [ProductSearchComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {}
