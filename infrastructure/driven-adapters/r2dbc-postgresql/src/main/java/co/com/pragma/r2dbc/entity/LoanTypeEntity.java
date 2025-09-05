package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "loan_types")
public class LoanTypeEntity {

    @Id
    private Long id;
    private String name;
    private Long minimumAmount;
    private Long maximumAmount;
    private Double interestRate;
}
