package it.unina.ingsw.model;

import java.util.List;

/** One page of results, plus the total number of matches to build the page links. */
public record PageResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements) {}
