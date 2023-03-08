package co.dalicious.domain.review.mapper;

import co.dalicious.domain.file.entity.embeddable.Image;
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
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "imageList", target = "images")
    @Mapping(source = "imageList", target = "imageOrigin")
    @Mapping(source = "reviewDto.content", target = "content")
    @Mapping(source = "reviewDto.content", target = "contentOrigin")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfaction")
    @Mapping(source = "reviewDto.satisfaction", target = "satisfactionOrigin")
    @Mapping(source = "reviewDto.forMakers", target = "forMakers")
    Reviews toEntity(ReviewReqDto reviewDto, User user, OrderItem orderItem, Food food, List<Image> imageList);

    @Mapping(source = "orderItemDailyFood.id", target = "orderItemId")
    @Mapping(source = "orderItemDailyFood.dailyFood.diningType.diningType", target = "diningType")
    @Mapping(source = "orderItemDailyFood.dailyFood.serviceDate", target = "serviceDate")
    @Mapping(target = "imageLocation", expression = "java(orderItemDailyFood.getDailyFood().getFood().getImages() == null || orderItemDailyFood.getDailyFood().getFood().getImages().isEmpty() ? null : orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation())")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.makers.name", target = "makersName")
    @Mapping(source = "orderItemDailyFood.dailyFood.food.name", target = "foodName")
    ReviewableItemListDto toDailyFoodResDto(OrderItemDailyFood orderItemDailyFood, long reviewDDAy);

    @Mapping(source = "reviews.images", target = "imageLocation", qualifiedByName = "getImagesLocation")
    @Mapping(source = "reviews.content", target = "content")
    @Mapping(source = "reviews.satisfaction", target = "satisfaction")
    @Mapping(source = "reviews.createdDateTime", target = "createDate")
    @Mapping(source = "reviews.updatedDateTime", target = "updateDate")
    @Mapping(source = "reviews.forMakers", target = "forMakers")
    @Mapping(source = "reviews.orderItem", target = "makersName", qualifiedByName = "getMakersName")
    @Mapping(source = "reviews.orderItem", target = "itemName", qualifiedByName = "getItemName")
    ReviewListDto toReviewListDto(Reviews reviews);

    @Named("getImagesLocation")
    default List<String>  getImagesLocation(List<Image> imageList) {
        if(imageList != null && !imageList.isEmpty()) {
            return imageList.stream().map(Image::getLocation).toList();
        }
        return null;
    }

    @Named("getMakersName")
    default String getMakersName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if(item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getMakers().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS);
    }

    @Named("getItemName")
    default String getItemName(OrderItem orderItem) {
        OrderItem item = (OrderItem) Hibernate.unproxy(orderItem);
        if(item instanceof OrderItemDailyFood orderItemDailyFood) {
            return orderItemDailyFood.getDailyFood().getFood().getName();
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND_ITEM);
    }
}
