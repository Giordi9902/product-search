package it.unina.ingsw.model;

import java.math.BigDecimal;

/** A product as it is sent to the client. */
public record ProductResponse(
		Integer id,
		String name,
		String description,
		BigDecimal price,
		String category,
		String image
) {
	public static ProductResponse from(Product product) {
		return new ProductResponse(
				product.id(),
				product.name(),
				product.description(),
				product.price(),
				product.category().name(),
				product.imagePath()
		);
	}
}
