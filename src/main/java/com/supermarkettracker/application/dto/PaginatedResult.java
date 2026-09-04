package com.supermarkettracker.application.dto;

import java.util.List;

public record PaginatedResult<T>(
    List<T> data,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PaginatedResult<T> of(List<T> data, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PaginatedResult<>(data, page, size, totalElements, totalPages, page < totalPages - 1, page > 0);
    }
}
