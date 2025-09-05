package co.com.pragma.model.loan;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Loan {

    private Long id;
    private String email;
    private Long amount;
    private Integer termMonths;
    private LoanType loanType;
    private Status status;
    private LocalDateTime createdAt;

}
