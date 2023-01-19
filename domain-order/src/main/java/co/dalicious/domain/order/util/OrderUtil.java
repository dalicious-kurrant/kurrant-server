package co.dalicious.domain.order.util;

import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.system.util.GenerateRandomNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderUtil {
    private final MembershipRepository membershipRepository;

    // 주문 코드 생성
    public static String generateOrderCode(OrderType orderType, BigInteger userId) {
        String code = switch (orderType) {
            case DAILYFOOD -> "S";
            case PRODUCT -> "P";
            case MEMBERSHIP -> "M";
        };
        LocalDate now = LocalDate.now();
        code += now.toString().replace("-", "");
        code += GenerateRandomNumber.idToString(userId.intValue());
        code += GenerateRandomNumber.create4DigitKey();
        return code;
    }

    public static void refundOrderMembership(User user, OrderMembership orderMembership) {
        if(orderMembership.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            orderMembership.getMembership().changeMembershipStatus(MembershipStatus.PROCESSING);
            orderMembership.getMembership().changeAutoPaymentStatus(true);
            user.changeMembershipStatus(true);
        }
        else if(orderMembership.getOrderStatus().equals(OrderStatus.AUTO_REFUND)) {
            orderMembership.getMembership().changeMembershipStatus(MembershipStatus.AUTO_REFUND);
            orderMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        } else if (orderMembership.getOrderStatus().equals(OrderStatus.MANUAL_REFUNDED)) {
            orderMembership.getMembership().changeMembershipStatus(MembershipStatus.BACK_OFFICE_REFUND);
            orderMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        }
    }
}
