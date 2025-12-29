package it.unina.ingsw.repository;

import it.unina.ingsw.model.Product;
import it.unina.ingsw.model.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
	private final JdbcTemplate jdbc;

	public int countByFilters(String name, String category, Double maxPrice) {
		String sql = "SELECT COUNT(*) FROM products WHERE 1=1";
		List<Object> params = new ArrayList<>();
		if (name != null && !name.isEmpty()) {
			sql += " AND name LIKE ?";
			params.add("%" + name + "%");
		}
		if( category != null && !category.isEmpty()) {
			sql += " AND category = ?";
			params.add(category.toUpperCase());
		}
		if (maxPrice != null) {
			sql += " AND price <= ?";
			params.add(maxPrice);
		}
		return jdbc.queryForObject(sql, params.toArray(), Integer.class);
	}

	public List<Product> search(String name, String category, Double maxPrice, int page, int size) {
		String sql = "SELECT * FROM products WHERE 1=1";
		List<Object> params = new ArrayList<>();
		if (name != null && !name.isEmpty()) {
			sql += " AND name LIKE ?";
			params.add("%" + name + "%");
		}
		if( category != null && !category.isEmpty()) {
			sql += " AND category = ?";
			params.add(category.toUpperCase());
		}
		if (maxPrice != null) {
			sql += " AND price <= ?";
			params.add(maxPrice);
		}
		sql += " LIMIT ? OFFSET ?";
		params.add(size);
		params.add(page * size);

		return jdbc.query(sql, params.toArray(), (rs, i)-> new Product(
				rs.getInt("id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getDouble("price"),
				ProductCategory.valueOf(rs.getString("category")),
				rs.getString("image_path")
		));
	}
}