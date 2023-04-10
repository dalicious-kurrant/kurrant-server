package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.dto.*;
import co.kurrant.app.public_api.model.SecurityUser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface OrderDailyFoodService {
    // 정기식사를 구매한다.
    BigInteger orderDailyFoods(SecurityUser securityUser, OrderItemDailyFoodReqDto orderItemDailyFoodReqDto);
    // 식사 일정을 조회한다.
    List<OrderDetailDto> findOrderByServiceDate(SecurityUser securityUser, LocalDate startDate, LocalDate endDate);
    // 구매 내역을 조회한다.
    List<OrderHistoryDto> findUserOrderDailyFoodHistory(SecurityUser securityUser, LocalDate startDate, LocalDate endDate, Integer orderType);
    // 구매 내역 상세를 조회한다.
    OrderDailyFoodDetailDto getOrderDailyFoodDetail(SecurityUser securityUser, BigInteger orderId);
    // 주문 전체를 환불한다.
    void cancelOrderDailyFood(SecurityUser securityUser, BigInteger orderId) throws IOException, ParseException;
    // 주문 상품을 환불한다
    void cancelOrderItemDailyFood(SecurityUser securityUser, BigInteger orderItemId) throws IOException, ParseException;

    Object orderDailyFoodsNice(SecurityUser securityUser, OrderItemDailyFoodByNiceReqDto orderItemDailyFoodReqDto) throws IOException, ParseException;

    void cancelOrderDailyFoodNice(SecurityUser securityUser, BigInteger id) throws IOException, ParseException;

    void cancelOrderItemDailyFoodNice(SecurityUser securityUser, BigInteger id) throws IOException, ParseException;

    void changingOrderItemOrderStatus(SecurityUser securityUser, BigInteger orderItemId);
}
