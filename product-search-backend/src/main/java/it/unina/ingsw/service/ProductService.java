package it.unina.ingsw.service;

import it.unina.ingsw.model.PageResponse;
import it.unina.ingsw.model.ProductCategory;
import it.unina.ingsw.model.ProductResponse;
import it.unina.ingsw.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public PageResponse<ProductResponse> search(String name, ProductCategory category, BigDecimal maxPrice,
			int page, int size) {
		List<ProductResponse> products = productRepository.search(name, category, maxPrice, page, size).stream()
				.map(ProductResponse::from)
				.toList();
		long total = productRepository.countByFilters(name, category, maxPrice);
		return new PageResponse<>(products, page, size, total);
	}
}
