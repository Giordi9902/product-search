package it.unina.ingsw.service;

import it.unina.ingsw.model.Product;
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

	public List<ProductResponse> search(String name, String category, Double maxPrice, int page, int size) {
		List<Product> products = productRepository.search(name, category, maxPrice, page, size);
		return products.stream()
				.map(productMapper::toProductResponse)
				.toList();
	}

	public int count(String name, String category, Double maxPrice) {
		return productRepository.countByFilters(name, category, maxPrice);
	}
}