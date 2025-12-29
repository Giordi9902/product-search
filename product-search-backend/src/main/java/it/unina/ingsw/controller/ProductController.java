package it.unina.ingsw.controller;

import it.unina.ingsw.model.PageResponse;
import it.unina.ingsw.model.ProductCategory;
import it.unina.ingsw.model.ProductResponse;
import it.unina.ingsw.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/search")
	public ResponseEntity<PageResponse<ProductResponse>> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	){
		List<ProductResponse> products = productService.search(name, category, maxPrice, page, size);
		int total = productService.count(name, category, maxPrice);
		PageResponse<ProductResponse> response = new PageResponse<>(
				products,
				page,
				size,
				total
		);
		return ResponseEntity.ok(response);
	}

    @GetMapping("/categories")
    public List<String> getCategories(){
        return Stream.of(ProductCategory.values()).map(ProductCategory::name).toList();
    }
}