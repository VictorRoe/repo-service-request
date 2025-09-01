package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateLoanRequestDTO;
import co.com.pragma.api.dto.LoanRequestResponseDTO;
import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {LoanType.class})
public interface LoanDTOMapper {

    @Mapping(target = "loanType", expression = "java(loan.getLoanType().name())")
    @Mapping(target = "status", expression = "java(loan.getStatus().name())")
    LoanRequestResponseDTO toResponse(Loan loan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "loanType", expression = "java(createLoanRequestDTO.loanType() == null ? null : LoanType.valueOf(createLoanRequestDTO.loanType().toUpperCase()))")
    Loan toModel(CreateLoanRequestDTO createLoanRequestDTO);

}
