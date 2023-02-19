package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.UserSupportPriceHistoryReqMapper;
import co.dalicious.domain.order.repository.PaymentCancelHistoryRepository;
import co.dalicious.domain.order.repository.UserSupportPriceHistoryRepository;
import co.dalicious.domain.order.service.OrderService;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.util.UserSupportPriceUtil;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.converter.RefundPriceDto;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final UserSupportPriceHistoryRepository userSupportPriceHistoryRepository;
    private final UserSupportPriceHistoryReqMapper userSupportPriceHistoryReqMapper;
    private final OrderUtil orderUtil;
    private final TossUtil tossUtil;

    @Override
    @Transactional
    public void cancelOrderDailyFood(OrderDailyFood order, User user) throws IOException, ParseException {
        BigDecimal price = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal point = BigDecimal.ZERO;

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItemDailyFood orderItemDailyFood = (OrderItemDailyFood) Hibernate.unproxy(orderItem);
            // 상태값이 이미 7L(취소)인지 확인
            if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
                throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
            }

            BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

            RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());
            price = price.add(refundPriceDto.getPrice());
            deliveryFee = deliveryFee.add(refundPriceDto.getDeliveryFee());
            point = point.add(refundPriceDto.getPoint());

            if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
                List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
                for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                    userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
                }
                UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
                if (userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                    userSupportPriceHistoryRepository.save(userSupportPriceHistory);
                }
            }

            // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
            if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0 || refundPriceDto.getPoint().compareTo(BigDecimal.ZERO) != 0) {
                PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(orderItemDailyFood, refundPriceDto, paymentCancelHistories);
                paymentCancelHistories.add(paymentCancelHistoryRepository.save(paymentCancelHistory));
            }
            orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

            if (refundPriceDto.getIsLastItemOfGroup()) {
                orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
            }
        }
        user.updatePoint(user.getPoint().add(point));
        tossUtil.cardCancelOne(order.getPaymentKey(), "전체 주문 취소", price.intValue());
    }

    @Override
    @Transactional
    public void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException {
        Order order = orderItemDailyFood.getOrder();

        if(!order.getUser().equals(user)) {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }

        // 상태값이 이미 7L(취소)인지 확인
        if (!orderItemDailyFood.getOrderStatus().equals(OrderStatus.COMPLETED) || orderItemDailyFood.getOrderItemDailyFoodGroup().getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }

        // 이전에 환불을 진행한 경우
        List<PaymentCancelHistory> paymentCancelHistories = paymentCancelHistoryRepository.findAllByOrderOrderByCancelDateTimeDesc(order);

        BigDecimal usedSupportPrice = UserSupportPriceUtil.getUsedSupportPrice(orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories());

        RefundPriceDto refundPriceDto = OrderUtil.getRefundPrice(orderItemDailyFood, paymentCancelHistories, order.getPoint());


        if (!refundPriceDto.isSameSupportPrice(usedSupportPrice)) {
            List<UserSupportPriceHistory> userSupportPriceHistories = orderItemDailyFood.getOrderItemDailyFoodGroup().getUserSupportPriceHistories();
            for (UserSupportPriceHistory userSupportPriceHistory : userSupportPriceHistories) {
                userSupportPriceHistory.updateMonetaryStatus(MonetaryStatus.REFUND);
            }
            UserSupportPriceHistory userSupportPriceHistory = userSupportPriceHistoryReqMapper.toEntity(orderItemDailyFood, refundPriceDto.getRenewSupportPrice());
            if (userSupportPriceHistory.getUsingSupportPrice().compareTo(BigDecimal.ZERO) != 0) {
                userSupportPriceHistoryRepository.save(userSupportPriceHistory);
            }
        }

        // 결제 정보가 없을 경우 -> 환불 요청 필요 없음.
        if (refundPriceDto.getPrice().compareTo(BigDecimal.ZERO) != 0) {
            PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemDailyFood(order.getPaymentKey(), "주문 마감 전 주문 취소", orderItemDailyFood, refundPriceDto);
            paymentCancelHistoryRepository.save(paymentCancelHistory);
        }

        user.updatePoint(user.getPoint().add(refundPriceDto.getPoint()));
        orderItemDailyFood.updateOrderStatus(OrderStatus.CANCELED);

        if (refundPriceDto.getIsLastItemOfGroup()) {
            orderItemDailyFood.getOrderItemDailyFoodGroup().updateOrderStatus(OrderStatus.CANCELED);
        }
    }
}
