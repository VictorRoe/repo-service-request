package co.com.pragma.model.loan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanDetails {

    private Loan loan;

    private String fullName;

    private Long userBaseSalary;

    private Long totalMonthlyDebt;
}
