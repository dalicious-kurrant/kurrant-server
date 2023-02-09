package co.kurrant.app.public_api.mapper.review;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.review.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewDto.image", target = "imageOrigin")
    @Mapping(source = "reviewDto.content", target = "content")
    @Mapping(source = "reviewDto.content", target = "contentOrigin")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfaction")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfactionOrigin")
    @Mapping(source = "reviewDto.forMakers", target = "forMakers")
    Reviews toEntity(ReviewDto reviewDto, User user, OrderItem orderItem, Food food);

}
