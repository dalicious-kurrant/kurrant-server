package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Employee;
import co.dalicious.domain.client.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {


    @Mapping(source = "corporation", target = "corporation")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    Employee toEntity(String email, String name, String phone, Corporation corporation);
}
