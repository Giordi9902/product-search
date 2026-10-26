package it.unina.ingsw.model;

import java.math.BigDecimal;

/** A row of the products table. */
public record Product(
		Integer id,
		String name,
		String description,
		BigDecimal price,
		ProductCategory category,
		String imagePath
) {}
