package co.com.pragma.api.dto;

import java.time.LocalDateTime;

public record LoanDetailDTO(
        Long loanId,
                            Long amount,
                            Integer termMonths,
                            String email,
                            String userName,
                            String loanType,
                            Double interestRate,
                            String status,
                            Long userBaseSalary,
                            Long totalMonthlyDebt,
                            LocalDateTime createdAt
) {
}
