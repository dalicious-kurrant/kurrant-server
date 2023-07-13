package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.dto.OrderDetailDto;
import co.dalicious.domain.order.dto.OrderItemDto;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.system.util.DateUtils;
import org.hibernate.Hibernate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigInteger;
import java.util.List;

@Mapper(componentModel = "spring", imports = {OrderDailyFood.class, DateUtils.class})
public interface OrderItemDailyFoodListMapper {

    default OrderItemDto toDto(OrderItemDailyFood orderItemDailyFood) {
        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setId(orderItemDailyFood.getId());
        orderItemDto.setDailyFoodId(orderItemDailyFood.getDailyFood().getId());
        orderItemDto.setDeliveryTime(DateUtils.timeToString(orderItemDailyFood.getDeliveryTime()));
        orderItemDto.setName(orderItemDailyFood.getDailyFood().getFood().getName());
        orderItemDto.setDailyFoodStatus(orderItemDailyFood.getDailyFood().getDailyFoodStatus().getCode());
        orderItemDto.setOrderStatus(Math.toIntExact(orderItemDailyFood.getOrderStatus().getCode()));
        orderItemDto.setMakers(orderItemDailyFood.getDailyFood().getFood().getMakers().getName());
        orderItemDto.setImage(orderItemDailyFood.getDailyFood().getFood().getImages() == null || orderItemDailyFood.getDailyFood().getFood().getImages().isEmpty() ? null : orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation());
        orderItemDto.setCount(orderItemDailyFood.getCount());
        orderItemDto.setGroupName(orderItemDailyFood.getDailyFood().getGroup().getName());
        // 상속 비교를 하기 위해 프록시 해제
        OrderDailyFood orderDailyFood = (OrderDailyFood) Hibernate.unproxy(orderItemDailyFood.getOrder());
        orderItemDto.setGroupName(orderDailyFood.getGroupName());
        orderItemDto.setSpotName(orderDailyFood.getSpotName());

        return orderItemDto;
    }

    default OrderDetailDto toOrderDetailDto(OrderDetailDto.OrderDetail orderDetail, List<OrderItemDto> orderItemDtoList, Integer totalCalorie) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();

        orderDetailDto.setServiceDate(DateUtils.localDateToString(orderDetail.getServiceDate()));
        orderDetailDto.setDiningType(orderDetail.getDiningType().getDiningType());
        orderDetailDto.setOrderItemDtoList(orderItemDtoList);
        orderDetailDto.setTotalCalorie(totalCalorie);

        return orderDetailDto;
    }

}
