package co.com.pragma.api.dto;

public record LoanRequestDTO(String documentId,
                                   Long amount,
                                   Integer termMonths,
                                   Long loanType
) {
}
