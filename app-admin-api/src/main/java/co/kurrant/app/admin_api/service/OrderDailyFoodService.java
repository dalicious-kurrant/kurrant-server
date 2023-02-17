package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.OrderDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OrderDailyFoodService {
    List<OrderDto.OrderItemDailyFoodList> retrieveOrder(Map<String, Object> parameters);
    OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode);
    List<GroupDto.Group> getGroup(Integer clientType);
    GroupDto getGroupInfo(BigInteger groupId);
    List<MakersDto.Makers> getMakers();
}
