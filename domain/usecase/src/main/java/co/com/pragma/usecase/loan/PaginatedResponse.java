package co.com.pragma.usecase.loan;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> items,
        int currentPage,
        long totalItems,
        int totalPages
) {
}
