package it.unina.ingsw.repository;

import it.unina.ingsw.model.Product;
import it.unina.ingsw.model.ProductCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductRepository {
	private final JdbcTemplate jdbc;

	public ProductRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	private static final RowMapper<Product> ROW_MAPPER = (rs, i) -> new Product(
			rs.getInt("id"),
			rs.getString("name"),
			rs.getString("description"),
			rs.getBigDecimal("price"),
			ProductCategory.valueOf(rs.getString("category")),
			rs.getString("image_path")
	);

	/** The WHERE clause and its parameters, built once and shared by the count and the page query. */
	private record Filter(String where, List<Object> params) {
		static Filter of(String name, ProductCategory category, BigDecimal maxPrice) {
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

	public long countByFilters(String name, ProductCategory category, BigDecimal maxPrice) {
		Filter filter = Filter.of(name, category, maxPrice);
		Long count = jdbc.queryForObject("SELECT COUNT(*) FROM products" + filter.where(), Long.class,
				filter.params().toArray());
		return count != null ? count : 0;
	}

	public List<Product> search(String name, ProductCategory category, BigDecimal maxPrice, int page, int size) {
		Filter filter = Filter.of(name, category, maxPrice);
		List<Object> params = new ArrayList<>(filter.params());
		params.add(size);
		params.add((long) page * size);
		// A fixed order (id breaks ties between equal names) keeps every product on exactly one page
		String sql = "SELECT * FROM products" + filter.where() + " ORDER BY name, id LIMIT ? OFFSET ?";
		return jdbc.query(sql, ROW_MAPPER, params.toArray());
	}
}
