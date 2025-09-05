package co.com.pragma.api.dto;

public record LoanResponseDTO(Long id,
                              String email,
                              Long amount,
                              Integer termMonths,
                              Long loanTypeId,
                              Long statusId,
                              String createdAt) {
}
