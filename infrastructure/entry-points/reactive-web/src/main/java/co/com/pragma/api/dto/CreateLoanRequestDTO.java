package co.com.pragma.api.dto;

public record CreateLoanRequestDTO(String documentId,
                                   Long amount,
                                   Integer termMonths,
                                   String loanType
) {
}
