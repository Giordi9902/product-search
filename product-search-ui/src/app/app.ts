import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ProductSearchComponent } from './product-search/product-search';

@Component({
  selector: 'app-root',
  imports: [ProductSearchComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {
  protected readonly title = signal('property-search');
}
