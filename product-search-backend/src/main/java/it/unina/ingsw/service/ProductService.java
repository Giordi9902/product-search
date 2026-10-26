package it.unina.ingsw.service;

import it.unina.ingsw.model.ProductCategory;
import it.unina.ingsw.model.ProductResponse;
import it.unina.ingsw.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductMapper productMapper;

	public List<ProductResponse> search(String name, ProductCategory category, Double maxPrice, int page, int size) {
		return productRepository.search(name, category, maxPrice, page, size).stream()
				.map(productMapper::toProductResponse)
				.toList();
	}

	public long count(String name, ProductCategory category, Double maxPrice) {
		return productRepository.countByFilters(name, category, maxPrice);
	}
}
