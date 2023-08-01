package co.dalicious.domain.user.mapper;

import co.dalicious.domain.client.entity.Department;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDepartmentMapper {


    @Mapping(source = "department", target = "department")
    @Mapping(source = "user", target = "user")
    UserDepartment toEntity(User user, Department department);
}
