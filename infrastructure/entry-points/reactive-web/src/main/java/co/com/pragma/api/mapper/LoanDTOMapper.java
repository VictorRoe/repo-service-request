package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanDetailDTO;
import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.dto.LoanResponseDTO;
import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface LoanDTOMapper {

    @Mapping(target = "loanType", expression = "java(new LoanType(dto.loanType(), null, null, null, null))")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Loan toDomain(LoanRequestDTO dto);

    @Mapping(target = "loanTypeId", source = "loanType.id")
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "createdAt", expression = "java(formatDate(loan.getCreatedAt()))")
    LoanResponseDTO toResponse(Loan loan);

    @Mapping(source = "loan.id", target = "loanId")
    @Mapping(source = "loan.amount", target = "amount")
    @Mapping(source = "loan.termMonths", target = "termMonths")
    @Mapping(source = "loan.email", target = "email")
    @Mapping(source = "loan.loanType.name", target = "loanType")
    @Mapping(source = "loan.loanType.interestRate", target = "interestRate")
    @Mapping(source = "loan.status.name", target = "status")
    @Mapping(source = "loan.createdAt", target = "createdAt")
    @Mapping(source = "fullName", target = "userName")
    @Mapping(source = "userBaseSalary", target = "userBaseSalary")
    @Mapping(source = "totalMonthlyDebt", target = "totalMonthlyDebt")
    LoanDetailDTO toDetailDTO(LoanDetails loanDetails);


    default String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss a");
        return dateTime != null ? dateTime.format(formatter) : null;
    }


}
