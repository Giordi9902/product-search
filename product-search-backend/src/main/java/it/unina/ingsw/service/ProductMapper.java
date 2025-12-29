package it.unina.ingsw.service;

import it.unina.ingsw.model.Product;
import it.unina.ingsw.model.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class ProductMapper {
	@Value("${app.images.path:./uploads/products/}")
	private String imagesPath;
	@Value("${app.images.web-prefix:/uploads/products/}")
	private String imagesWebPrefix;
	@Value("${app.server.url:http://localhost:8080}")
	private String serverUrl;

	public ProductResponse toProductResponse(Product product){
		String imageUrl = null;
		String path = product.getImagePath();
		if (path != null && !path.isBlank()) {
			String normalized = path;
			// If DB stored a web path like "/uploads/products/file.jpg", strip prefix to get filename
			if (normalized.startsWith("/uploads/")) {
				normalized = normalized.replaceFirst("^/uploads/products/?", "");
			}
			// Build a web-accessible URL pointing to the static resource handler
			String prefix = imagesWebPrefix != null ? imagesWebPrefix : "/uploads/products/";
			if (!prefix.endsWith("/")) prefix = prefix + "/";
			String filename = normalized;
			// If normalized may contain path segments, trim leading slashes
			if (filename.startsWith("/")) filename = filename.substring(1);
			// Ensure serverUrl has no trailing slash
			String base = serverUrl != null ? serverUrl.replaceAll("/+$", "") : "http://localhost:8080";
			imageUrl = base + prefix + filename;
		}
		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getCategory().name(),
				imageUrl
		);
	}
}