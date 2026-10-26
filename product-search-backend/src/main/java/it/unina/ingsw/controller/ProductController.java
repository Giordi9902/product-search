package it.unina.ingsw.controller;

import it.unina.ingsw.model.PageResponse;
import it.unina.ingsw.model.ProductCategory;
import it.unina.ingsw.model.ProductResponse;
import it.unina.ingsw.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/products")
public class ProductController {

	static final int MAX_PAGE_SIZE = 100;

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/search")
	public PageResponse<ProductResponse> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		if (page < 0) throw badRequest("page must be 0 or greater");
		if (size < 1 || size > MAX_PAGE_SIZE) throw badRequest("size must be between 1 and " + MAX_PAGE_SIZE);
		if (maxPrice != null && maxPrice.signum() < 0) throw badRequest("maxPrice cannot be negative");
		return productService.search(name, parseCategory(category), maxPrice, page, size);
	}

	@GetMapping("/categories")
	public List<String> getCategories() {
		return Stream.of(ProductCategory.values()).map(ProductCategory::name).toList();
	}

	/** An unknown category is a client error, not a search with no results. */
	private static ProductCategory parseCategory(String category) {
		if (category == null || category.isBlank()) return null;
		try {
			return ProductCategory.valueOf(category.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw badRequest("unknown category: " + category + "; allowed values: "
					+ Arrays.toString(ProductCategory.values()));
		}
	}

	private static ResponseStatusException badRequest(String reason) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
	}
}
