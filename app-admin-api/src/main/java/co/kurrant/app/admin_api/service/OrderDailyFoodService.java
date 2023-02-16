package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.OrderDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OrderDailyFoodService {
    List<OrderDto.OrderItemDailyFoodList> retrieveOrder(Map<String, Object> parameters);
    void getOrderDetail(BigInteger orderItemDailyFoodId);
    List<GroupDto.Group> getGroup(Integer clientType);
    GroupDto getGroupInfo(BigInteger groupId);

}
