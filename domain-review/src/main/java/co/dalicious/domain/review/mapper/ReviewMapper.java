package co.dalicious.domain.review.mapper;

import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.review.dto.ReviewListDto;
import co.dalicious.domain.review.dto.ReviewReqDto;
import co.dalicious.domain.review.dto.ReviewableItemListDto;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "reviewDto.image", target = "imageOrigin")
    @Mapping(source = "reviewDto.content", target = "content")
    @Mapping(source = "reviewDto.content", target = "contentOrigin")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfaction")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfactionOrigin")
    @Mapping(source = "reviewDto.forMakers", target = "forMakers")
    Reviews toEntity(ReviewReqDto reviewDto, User user, OrderItem orderItem, Food food);

    @Mapping(source = "orderItemDailyFood.dailyFood.id", target = "itemId")
    @Mapping(source = "orderItemDailyFood.dailyFood.diningType.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.dailyFood.serviceDate", target = "serviceDate")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.image.location", target = "imageLocation")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.makers.name", target = "makersName")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.name", target = "foodName")
    ReviewableItemListDto toDailyFoodResDto(OrderItemDailyFood orderItemDailyFood, long reviewDDAy);

    @Mapping(source = "reviews.image.location", target = "imageLocation")
    @Mapping(source = "reviews.content", target = "content")
    @Mapping(source = "reviews.satisfaction", target = "satisfaction")
    @Mapping(source = "reviews.createdDateTime", target = "createDate")
    @Mapping(source = "reviews.updatedDateTime", target = "updateDate")
    @Mapping(source = "reviews.forMakers", target = "forMakers")
    @Mapping(source = "reviews.orderItem", target = "makersName", qualifiedByName = "getMakersName")
    @Mapping(source = "reviews.orderItem", target = "itemName", qualifiedByName = "getItemName")
    ReviewListDto toReviewListDto(Reviews reviews);

    @Named("getMakersName")
    default String getMakersName(OrderItem orderItem) {
        String makersName = null;
        if(orderItem instanceof OrderItemDailyFood) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            return makersName = orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
        }

        throw new ApiException(ExceptionEnum.NOT_FOND_MAKERS);
    }

    @Named("getItemName")
    default String getItemName(OrderItem orderItem) {
        String itemName = null;
        if(orderItem instanceof OrderItemDailyFood) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) orderItem;
            return itemName = orderItemDailyFood.getDailyFood().getFood().getName();
        }

        throw new ApiException(ExceptionEnum.NOT_FOND_ITEM);
    }
}
