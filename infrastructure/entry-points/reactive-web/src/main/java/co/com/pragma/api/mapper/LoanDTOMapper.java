package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.dto.LoanResponseDTO;
import co.com.pragma.api.dto.LoanTypeDTO;
import co.com.pragma.api.dto.StatusDTO;
import co.com.pragma.model.loan.Loan;
import co.com.pragma.model.loan.LoanType;
import co.com.pragma.model.loan.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanDTOMapper {

    @Mapping(target = "loanType", expression = "java(new LoanType(dto.loanType(), null, null, null, null))")
    @Mapping(target = "status", ignore = true) // Se asigna luego en el caso de uso
    @Mapping(target = "id", ignore = true) // Lo asigna la DB
    @Mapping(target = "createdAt", ignore = true) // Lo asigna el caso de uso
    Loan toDomain(LoanRequestDTO dto);


    LoanResponseDTO toResponse(Loan loan);

    LoanTypeDTO toLoanTypeDTO(LoanType loanType);
    StatusDTO toStatusDTO(Status status);



}
