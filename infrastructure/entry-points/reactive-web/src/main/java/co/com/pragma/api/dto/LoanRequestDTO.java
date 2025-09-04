package co.com.pragma.api.dto;

public record LoanRequestDTO(String email,
                                   Long amount,
                                   Integer termMonths,
                                   Long loanType
) {
}
