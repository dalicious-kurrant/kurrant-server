package co.kurrant.app.admin_api.service;

import co.dalicious.domain.order.dto.ExtraOrderDto;
import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.order.dto.OrderDto;
import org.geolatte.geom.V;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDailyFoodService {
    List<OrderDto.OrderItemDailyFoodList> retrieveOrder(Map<String, Object> parameters);
    OrderDailyFoodByMakersDto.ByPeriod retrieveOrderByMakers(Map<String, Object> parameters);
    OrderDto.OrderDailyFoodDetail getOrderDetail(String orderCode);
    List<GroupDto.Group> getGroup(Integer clientType);
    GroupDto getGroupInfo(BigInteger groupId);
    List<MakersDto.Makers> getMakers();
    void cancelOrder(BigInteger orderId) throws IOException, ParseException;
    void changeOrderStatus(OrderDto.StatusAndIdList statusAndIdList);
    void cancelOrderItems(List<BigInteger> orderItemIdList) throws IOException, ParseException;

    void cancelOrderNice(BigInteger orderId) throws IOException, ParseException;

    void cancelOrderItemsNice(List<BigInteger> idList) throws IOException, ParseException;

    List<ExtraOrderDto.DailyFoodList> getExtraDailyFoods(LocalDate startDate, LocalDate endDate, BigInteger groupId);
    void postExtraOrderItems(List<ExtraOrderDto.Request> orderDtos);
    List<ExtraOrderDto.Response> getExtraOrders(Map<String, Object> parameters);
    void refundExtraOrderItems(BigInteger id);
}
