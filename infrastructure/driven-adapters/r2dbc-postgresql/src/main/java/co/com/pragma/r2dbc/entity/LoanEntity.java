package co.com.pragma.r2dbc.entity;

import co.com.pragma.model.loan.LoanType;
import co.com.pragma.model.loan.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "loan_request")
public class LoanEntity {

    @Id
    private Long id;
    private String documentId;
    private Long amount;
    private Integer termMonths;
    private LoanType loanType;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
