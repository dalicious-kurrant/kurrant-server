package co.kurrant.app.public_api.mapper.user;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper <UserDto, User>{
}