package it.unina.ingsw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests of the whole application against a real, empty PostgreSQL started by Testcontainers
 * (Docker required): the startup itself checks that schema and seed data load from scratch.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ProductSearchIntegrationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

	@Autowired
	MockMvc mvc;

	@Autowired
	JdbcTemplate jdbc;

	@Test
	void startsOnAnEmptyDatabaseAndLoadsTheProducts() throws Exception {
		mvc.perform(get("/products/search"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(15))
				.andExpect(jsonPath("$.content.length()").value(10));
	}

	@Test
	void theNextProductGetsAFreeId() {
		Integer id = jdbc.queryForObject(
				"INSERT INTO products (name, category, price) VALUES ('Prova', 'BOOKS', 1) RETURNING id", Integer.class);
		assertThat(id).isEqualTo(16);
		jdbc.update("DELETE FROM products WHERE id = ?", id);
	}

	@Test
	void nameSearchIgnoresCase() throws Exception {
		mvc.perform(get("/products/search").param("name", "mouse"))
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].name").value("Wireless Mouse"));
	}

	@Test
	void percentAndUnderscoreMatchLiterally() throws Exception {
		mvc.perform(get("/products/search").param("name", "%"))
				.andExpect(jsonPath("$.totalElements").value(0));
		mvc.perform(get("/products/search").param("name", "_"))
				.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	void categoryAndMaxPriceCombine() throws Exception {
		mvc.perform(get("/products/search").param("category", "accessories").param("maxPrice", "30"))
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.content[*].name").value(contains(
						"Laptop Sleeve", "Smartphone Stand", "Wireless Charging Pad")));
	}

	@Test
	void pagesCoverEveryProductOnceInNameOrder() throws Exception {
		List<String> seen = new ArrayList<>();
		for (int page = 0; page < 3; page++) {
			String body = mvc.perform(get("/products/search").param("size", "5").param("page", String.valueOf(page)))
					.andReturn().getResponse().getContentAsString();
			seen.addAll(JsonPath.read(body, "$.content[*].name"));
		}
		assertThat(seen).hasSize(15).doesNotHaveDuplicates().isSorted();
	}

	@Test
	void pricesKeepTheirCentsAndImagesAreServed() throws Exception {
		mvc.perform(get("/products/search").param("name", "mouse"))
				.andExpect(jsonPath("$.content[0].price").value(25.99))
				.andExpect(jsonPath("$.content[0].image").value("/uploads/products/wireless_mouse.jpg"));
		mvc.perform(get("/uploads/products/wireless_mouse.jpg"))
				.andExpect(status().isOk());
	}

	@Test
	void invalidParametersGive400() throws Exception {
		mvc.perform(get("/products/search").param("page", "-1")).andExpect(status().isBadRequest());
		mvc.perform(get("/products/search").param("size", "0")).andExpect(status().isBadRequest());
		mvc.perform(get("/products/search").param("size", "1000")).andExpect(status().isBadRequest());
		mvc.perform(get("/products/search").param("maxPrice", "-5")).andExpect(status().isBadRequest());
		mvc.perform(get("/products/search").param("category", "SHOES"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail", containsString("unknown category")));
	}

	@Test
	void categoriesComeFromTheEnum() throws Exception {
		mvc.perform(get("/products/categories"))
				.andExpect(jsonPath("$.length()").value(7))
				.andExpect(jsonPath("$[0]").value("ELECTRONICS"));
	}
}
