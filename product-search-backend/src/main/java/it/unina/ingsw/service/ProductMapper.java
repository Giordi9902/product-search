package it.unina.ingsw.service;

import it.unina.ingsw.model.Product;
import it.unina.ingsw.model.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class ProductMapper {
	@Value("${app.images.web-prefix:/uploads/products/}")
	private String imagesWebPrefix;
	/** Public base URL of the server; empty = taken from the current request (real host and port). */
	@Value("${app.server.url:}")
	private String serverUrl;

	public ProductResponse toProductResponse(Product product){
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getCategory() != null ? product.getCategory().name() : null,
				imageUrl(product.getImagePath())
		);
	}

	private String imageUrl(String path) {
		if (path == null || path.isBlank()) return null;
		// The DB stores a web path like "/uploads/products/file.jpg": keep only the file name
		String filename = path.replaceFirst("^/?uploads/products/?", "").replaceFirst("^/+", "");
		String prefix = imagesWebPrefix.endsWith("/") ? imagesWebPrefix : imagesWebPrefix + "/";
		String base = serverUrl.isBlank()
				? ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()
				: serverUrl;
		return base.replaceAll("/+$", "") + prefix + filename;
	}
}
