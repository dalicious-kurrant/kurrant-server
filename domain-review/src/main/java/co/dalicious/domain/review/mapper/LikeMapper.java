package co.dalicious.domain.review.mapper;

import co.dalicious.domain.review.entity.Like;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    @Mapping(source = "reviewId", target = "reviewId")
    @Mapping(source = "user", target = "user")
    Like toEntity(User user, BigInteger reviewId);
}
