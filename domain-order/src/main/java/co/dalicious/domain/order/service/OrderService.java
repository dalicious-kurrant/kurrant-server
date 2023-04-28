package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.util.PeriodDto;
import org.json.simple.parser.ParseException;

import javax.transaction.Transactional;
import java.io.IOException;

public interface OrderService {
    // 정기식사 주문을 모두 취소한다
    void cancelOrderDailyFood(OrderDailyFood orderDailyFood, User user) throws IOException, ParseException;
    // 정기식사 주문 상품 하나를 취소한다.
    void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;
    // 멤버십 결제를 한다.
    void payMembership(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType);
    void updateFailedMembershipPayment(Membership membership);

    void cancelOrderDailyFoodNice(OrderDailyFood orderDailyFood, User user) throws IOException, ParseException;

    void cancelOrderItemDailyFoodNice(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;

    void payMembershipNice(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType ofCode) throws IOException, ParseException;
}
