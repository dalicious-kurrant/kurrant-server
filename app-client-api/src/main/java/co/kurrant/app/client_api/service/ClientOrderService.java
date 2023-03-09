package co.kurrant.app.client_api.service;

import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.client_api.dto.GroupDto;
import co.kurrant.app.client_api.model.SecurityUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface ClientOrderService {
    GroupDto getGroupInfo(SecurityUser securityUser);
    OrderDto.GroupOrderItemDailyFoodList getOrder(SecurityUser securityUser, Map<String, Object> parameters);
    OrderDto.OrderDailyFoodDetail getOrderDetail(SecurityUser securityUser, String orderCode);
    List<OrderDto.OrderItemStatic> getOrderStatistic(SecurityUser securityUser, @RequestParam Map<String, Object> parameters);
}
