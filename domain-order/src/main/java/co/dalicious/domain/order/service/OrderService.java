package co.dalicious.domain.order.service;

import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.system.util.PeriodDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.transaction.annotation.Propagation;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

public interface OrderService {
    // 정기식사 주문을 한다.
    JSONObject payDailyFood(User user, CreditCardInfo creditCardInfo, Integer amount, String orderCode, String orderName) throws IOException, ParseException;
    // 정기식사 주문을 모두 취소한다
    Set<BigInteger> cancelOrderDailyFoodNice(OrderDailyFood orderDailyFood, User user) throws IOException, ParseException;
    // 정기식사 주문 상품 하나를 취소한다.
    void cancelOrderItemDailyFoodNice(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;
    // 백오피스 정기식사 주문 상품 하나를 취소한다.(상태값 무관)
    void adminCancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;
    // 멤버십 결제를 한다.
    void payMembershipNice(Membership membership, PaymentType ofCode) throws IOException, ParseException;
    // 멤버십 결제를 한다.
    void payMembershipNice(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType ofCode) throws IOException, ParseException;


    // FIXME: 토스 결제    
    void cancelOrderDailyFood(OrderDailyFood orderDailyFood, User user) throws IOException, ParseException;
    void cancelOrderItemDailyFood(OrderItemDailyFood orderItemDailyFood, User user) throws IOException, ParseException;
    void payMembership(User user, MembershipSubscriptionType membershipSubscriptionType, PeriodDto periodDto, PaymentType paymentType);
    void updateFailedMembershipPayment(Membership membership);

}
