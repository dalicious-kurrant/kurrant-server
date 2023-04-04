package co.kurrant.app.client_api.service;

import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.domain.order.dto.GroupDto;
import co.kurrant.app.client_api.model.SecurityUser;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClientOrderService {
    GroupDto getGroupInfo(SecurityUser securityUser, Map<String, Object> parameters);
    OrderDto.GroupOrderItemDailyFoodList getOrder(SecurityUser securityUser, Map<String, Object> parameters);
    OrderDto.OrderDailyFoodDetail getOrderDetail(SecurityUser securityUser, String orderCode);
    List<OrderDto.OrderItemStatic> getOrderStatistic(SecurityUser securityUser, @RequestParam Map<String, Object> parameters);
    List<ExtraOrderDto.DailyFoodList> getExtraDailyFoods(SecurityUser securityUser, LocalDate startDate, LocalDate endDate);
    void postExtraOrderItems(SecurityUser securityUser, List<ExtraOrderDto.Request> orderDtos);
    List<ExtraOrderDto.Response> getExtraOrders(SecurityUser securityUser);
    void refundExtraOrderItems(SecurityUser securityUser, BigInteger id);
}
