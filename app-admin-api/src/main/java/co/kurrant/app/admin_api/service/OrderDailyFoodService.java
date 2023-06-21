package co.kurrant.app.admin_api.service;

import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.order.dto.OrderDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDailyFoodService {
    List<OrderDto.OrderItemDailyFoodGroupList> retrieveOrder(Map<String, Object> parameters);
    OrderDailyFoodByMakersDto.ByPeriod retrieveOrderByMakers(Map<String, Object> parameters);
    OrderDailyFoodByMakersDto.ByPeriod retrieveOrderCountByMakersAndDelivery(Map<String, Object> parameters);
    OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode);
    List<GroupDto.Group> getGroup(Integer spotType);
    GroupDto getGroupInfo(BigInteger groupId);
    List<MakersDto.Makers> getMakers();
    void changeOrderStatus(OrderDto.StatusAndIdList statusAndIdList) throws IOException, ParseException;
    void cancelOrderNice(BigInteger orderId) throws IOException, ParseException;
    String cancelOrderItemsNice(List<BigInteger> idList) throws IOException, ParseException;

    List<ExtraOrderDto.DailyFoodList> getExtraDailyFoods(LocalDate startDate, LocalDate endDate, BigInteger groupId);
    void postExtraOrderItems(List<ExtraOrderDto.Request> orderDtos);
    List<ExtraOrderDto.Response> getExtraOrders(Map<String, Object> parameters);
    void refundExtraOrderItems(BigInteger id);

    // Toss 환불
    void cancelOrderToss(BigInteger orderId) throws IOException, ParseException;
    void cancelOrderItemsToss(List<BigInteger> orderItemIdList) throws IOException, ParseException;
}
