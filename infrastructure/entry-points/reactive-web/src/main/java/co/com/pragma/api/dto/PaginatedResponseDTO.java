package co.com.pragma.api.dto;

import java.util.List;

public record PaginatedResponseDTO<T>(
        List<T> items,
        int currentPage,
        long totalItems,
        int totalPages
) {
}
