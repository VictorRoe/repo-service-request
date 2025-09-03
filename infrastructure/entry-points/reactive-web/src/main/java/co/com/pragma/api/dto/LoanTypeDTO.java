package co.com.pragma.api.dto;

public record LoanTypeDTO( Long id,
                           String name,
                           Long minimumAmount,
                           Long maximumAmount,
                           Double interestRate) {
}
