package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.loan.Loan;
import co.com.pragma.r2dbc.entity.LoanEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanEntityMapper {

    @Mapping(target = "loanTypeId", source = "loanType.id")
    @Mapping(target = "statusId", source = "status.id")
    LoanEntity toEntity(Loan loan);

    @Mapping(target = "loanType", expression = "java(new LoanType(entity.getLoanTypeId(), null, null, null, null))")
    @Mapping(target = "status", expression = "java(new Status(entity.getStatusId(), null, null))")
    Loan toDomain(LoanEntity entity);
}


