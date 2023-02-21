package co.kurrant.app.admin_api.service;

import co.dalicious.domain.order.dto.OrderDailyFoodByMakersDto;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.kurrant.app.admin_api.dto.OrderDto;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigInteger;
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
    void cancelOrderItems(List<BigInteger> orderItemIdList) throws IOException, ParseException;
}
