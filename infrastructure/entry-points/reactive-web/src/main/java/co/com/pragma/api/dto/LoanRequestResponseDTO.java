package co.com.pragma.api.dto;

import java.time.LocalDateTime;

public record LoanRequestResponseDTO(Long id,
                                     String documentId,
                                     Long amount,
                                     Integer termMonths,
                                     String loanType,
                                     String status,
                                     LocalDateTime createdAt) {
}
