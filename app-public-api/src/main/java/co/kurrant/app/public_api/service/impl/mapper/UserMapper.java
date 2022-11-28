package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.Apartment;
import co.dalicious.domain.user.entity.Corporation;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper <UserDto, User>{
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}