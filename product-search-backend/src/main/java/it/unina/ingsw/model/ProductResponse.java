package it.unina.ingsw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse{
	private Integer id;
	private String name;
	private String description;
	private Double price;
	private String category;
	private String image;
}