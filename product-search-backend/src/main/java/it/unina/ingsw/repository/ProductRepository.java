package it.unina.ingsw.repository;

import it.unina.ingsw.model.Product;
import it.unina.ingsw.model.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
	private final JdbcTemplate jdbc;

	private static final RowMapper<Product> ROW_MAPPER = (rs, i) -> new Product(
			rs.getInt("id"),
			rs.getString("name"),
			rs.getString("description"),
			rs.getDouble("price"),
			rs.getString("category") != null ? ProductCategory.valueOf(rs.getString("category")) : null,
			rs.getString("image_path")
	);

	/** The WHERE clause and its parameters, built once and shared by the count and the page query. */
	private record Filter(String where, List<Object> params) {
		static Filter of(String name, ProductCategory category, Double maxPrice) {
			StringBuilder where = new StringBuilder(" WHERE 1=1");
			List<Object> params = new ArrayList<>();
			if (name != null && !name.isBlank()) {
				// ILIKE ignores case; % and _ typed by the user must match literally, not as wildcards
				where.append(" AND name ILIKE ? ESCAPE '\\'");
				params.add("%" + escapeLike(name.trim()) + "%");
			}
			if (category != null) {
				where.append(" AND category = ?");
				params.add(category.name());
			}
			if (maxPrice != null) {
				where.append(" AND price <= ?");
				params.add(maxPrice);
			}
			return new Filter(where.toString(), params);
		}
	}

	static String escapeLike(String text) {
		return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
	}

	public long countByFilters(String name, ProductCategory category, Double maxPrice) {
		Filter filter = Filter.of(name, category, maxPrice);
		Long count = jdbc.queryForObject("SELECT COUNT(*) FROM products" + filter.where(), Long.class,
				filter.params().toArray());
		return count != null ? count : 0;
	}

	public List<Product> search(String name, ProductCategory category, Double maxPrice, int page, int size) {
		Filter filter = Filter.of(name, category, maxPrice);
		List<Object> params = new ArrayList<>(filter.params());
		params.add(size);
		params.add((long) page * size);
		// Without ORDER BY PostgreSQL returns rows in whatever order it likes, so
		// LIMIT/OFFSET may repeat or skip products from one page to the next.
		String sql = "SELECT * FROM products" + filter.where() + " ORDER BY name, id LIMIT ? OFFSET ?";
		return jdbc.query(sql, ROW_MAPPER, params.toArray());
	}
}
