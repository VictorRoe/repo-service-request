package co.com.pragma.usecase.loan;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanType;
import co.com.pragma.model.loan.Status;
import co.com.pragma.model.loan.gateways.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoanUseCaseTest {

    private LoanRepository repository;
    private LoanUseCase loanUseCase;

    @BeforeEach
    void setUp() {
        repository = mock(LoanRepository.class);
        loanUseCase = new LoanUseCase(repository);
    }

    @Test
    void shouldRegisterLoanSuccessfully() {
        Loan loanRequest = Loan.builder()
                .loanType(LoanType.builder().id(1L).name("Personal Loan").build())
                .amount(5000L)
                .build();

        Loan savedLoan = Loan.builder()
                .loanType(loanRequest.getLoanType())
                .amount(loanRequest.getAmount())
                .status(Status.builder().id(1L).name("PENDING").build())
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.saveLoanRequest(any(Loan.class))).thenReturn(Mono.just(savedLoan));


        StepVerifier.create(loanUseCase.register(loanRequest))
                .assertNext(result -> {
                    assertThat(result.getLoanType().getId()).isEqualTo(1L);
                    assertThat(result.getStatus().getName()).isEqualTo("PENDING");
                    assertThat(result.getAmount()).isEqualTo(5000L);
                })
                .verifyComplete();

        // Verifica que el repositorio fue llamado
        verify(repository, times(1)).saveLoanRequest(any(Loan.class));
    }

    @Test
    void shouldFailWhenLoanTypeIsNull() {
        // Arrange
        Loan loanRequest = Loan.builder()
                .amount(5000L)
                .build();

        // Act & Assert
        StepVerifier.create(loanUseCase.register(loanRequest))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException &&
                                error.getMessage().equals("Loan type is required")
                )
                .verify();

        verify(repository, never()).saveLoanRequest(any());
    }

}
