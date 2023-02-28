package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.entity.EmployeeHistory;
import co.dalicious.domain.client.entity.enums.EmployeeHistoryType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface EmployeeHistoryMapper {

    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "type", target = "type")
    EmployeeHistory toEntity(BigInteger userId, String name, String email, String phone, EmployeeHistoryType type);
}
