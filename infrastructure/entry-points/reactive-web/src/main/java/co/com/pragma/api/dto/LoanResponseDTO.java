package co.com.pragma.api.dto;

import java.time.LocalDateTime;

public record LoanResponseDTO(Long id,
                              String documentId,
                              Long amount,
                              Integer termMonths,
                              LoanTypeDTO loanType,
                              StatusDTO status,
                              LocalDateTime createdAt) {
}
