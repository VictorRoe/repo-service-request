package co.com.pragma.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "loans")
public class LoanEntity {

    @Id
    private Long id;
    private String email;
    private Long amount;
    private Integer termMonths;
    @Column("loan_type_id")
    private Long loanTypeId;
    @Column("status_id")
    private Long statusId;
    @Column("created_at")
    private LocalDateTime createdAt;
}
