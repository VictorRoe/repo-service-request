package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanRequestDTO;
import co.com.pragma.api.dto.LoanResponseDTO;
import co.com.pragma.model.loan.Loan;
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


    default String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss a");
        return dateTime != null ? dateTime.format(formatter) : null;
    }


}
