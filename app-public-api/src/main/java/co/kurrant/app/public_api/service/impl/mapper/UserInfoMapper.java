package co.kurrant.app.public_api.service.impl.mapper;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.UserInfoDto;
import org.mapstruct.factory.Mappers;

public interface UserInfoMapper extends GenericMapper<UserInfoDto, User> {
    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);
}
