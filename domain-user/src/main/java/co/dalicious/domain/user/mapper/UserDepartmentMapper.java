package co.dalicious.domain.user.mapper;

import co.dalicious.domain.client.entity.Department;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserDepartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface UserDepartmentMapper {


    @Mapping(source = "department", target = "department")
    @Mapping(source = "user", target = "user")
    UserDepartment toEntity(User user, Department department);
}
