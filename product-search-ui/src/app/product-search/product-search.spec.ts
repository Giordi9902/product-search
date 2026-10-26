import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductSearchComponent } from './product-search';
import { PageResponse, Product } from '../services/product.model';

const API = '/products';

function page(content: Partial<Product>[], totalElements: number, pageNumber = 0): PageResponse<Product> {
  return { content: content as Product[], pageNumber, pageSize: 5, totalElements };
}

describe('ProductSearchComponent', () => {
  let fixture: ComponentFixture<ProductSearchComponent>;
  let component: ProductSearchComponent;
  let http: HttpTestingController;

  /** The pending search request, with its parameters. */
  const pendingSearch = () => http.expectOne((r) => r.url === `${API}/search`);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProductSearchComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    fixture = TestBed.createComponent(ProductSearchComponent);
    component = fixture.componentInstance;
    http = TestBed.inject(HttpTestingController);
    fixture.detectChanges(); // ngOnInit: first search and categories
    http.expectOne(`${API}/categories`).flush(['TOYS', 'BOOKS']);
    pendingSearch().flush(page([{ id: 1, name: 'A' }], 12));
  });

  afterEach(() => http.verify());

  it('sorts the categories', () => {
    expect(component.categories).toEqual(['BOOKS', 'TOYS']);
  });

  it('a new search starts again from the first page', () => {
    component.onPageChange(2);
    pendingSearch().flush(page([{ id: 11, name: 'K' }], 12, 2));
    expect(component.page).toBe(2);

    component.name = 'mouse';
    component.search();
    const req = pendingSearch();
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('name')).toBe('mouse');
    req.flush(page([{ id: 1, name: 'Wireless Mouse' }], 1));
    expect(component.page).toBe(0);
  });

  it('sends maxPrice even when it is 0', () => {
    component.maxPrice = 0;
    component.search();
    const req = pendingSearch();
    expect(req.request.params.get('maxPrice')).toBe('0');
    req.flush(page([], 0));
  });

  it('ignores the answer of a search that has been replaced', () => {
    component.name = 'old';
    component.search();
    const old = pendingSearch();
    component.name = 'new';
    component.search();
    expect(old.cancelled).toBeTrue();
    pendingSearch().flush(page([{ id: 2, name: 'New' }], 1));
    expect(component.products.map((p) => p.name)).toEqual(['New']);
  });

  it('shows the message of a 400 from the server', () => {
    component.search();
    pendingSearch().flush(
      { detail: 'size must be between 1 and 100' },
      { status: 400, statusText: 'Bad Request' }
    );
    fixture.detectChanges();
    const alert = (fixture.nativeElement as HTMLElement).querySelector('[role=alert]');
    expect(alert?.textContent).toContain('size must be between 1 and 100');
  });

  it('tells when the server cannot be reached', () => {
    component.search();
    pendingSearch().error(new ProgressEvent('error'), { status: 0 });
    expect(component.error).toContain('Cannot reach the server');
  });
});
