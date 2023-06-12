package co.dalicious.domain.client.mapper;

import co.dalicious.domain.client.entity.Department;
import co.dalicious.domain.client.entity.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    @Mapping(source = "name", target = "name")
    @Mapping(source = "group", target = "group")
    Department toEntity(Group group, String name);
}
