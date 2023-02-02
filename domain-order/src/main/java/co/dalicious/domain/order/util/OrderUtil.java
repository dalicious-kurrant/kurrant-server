package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.food.dto.DiscountDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.system.util.GenerateRandomNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderUtil {

    // 주문 코드 생성
    public static String generateOrderCode(OrderType orderType, BigInteger userId) {
        String code = switch (orderType) {
            case DAILYFOOD -> "S";
            case PRODUCT -> "P";
            case MEMBERSHIP -> "M";
            case CATERING -> "C";
        };
        LocalDate now = LocalDate.now();
        code += now.toString().replace("-", "");
        code += GenerateRandomNumber.idToString(userId.intValue());
        code += GenerateRandomNumber.create4DigitKey();
        return code;
    }

    public static void refundOrderMembership(User user, OrderItemMembership orderItemMembership) {
        if(orderItemMembership.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.PROCESSING);
            orderItemMembership.getMembership().changeAutoPaymentStatus(true);
            user.changeMembershipStatus(true);
        }
        else if(orderItemMembership.getOrderStatus().equals(OrderStatus.AUTO_REFUND)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.AUTO_REFUND);
            orderItemMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        } else if (orderItemMembership.getOrderStatus().equals(OrderStatus.MANUAL_REFUNDED)) {
            orderItemMembership.getMembership().changeMembershipStatus(MembershipStatus.BACK_OFFICE_REFUND);
            orderItemMembership.getMembership().changeAutoPaymentStatus(false);
            user.changeMembershipStatus(false);
        }
    }

    public static BigDecimal discountedPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf((100.0 - discountRate) / 100));
    }

    public static BigDecimal discountPriceByRate(BigDecimal price, Integer discountRate) {
        return price.multiply(BigDecimal.valueOf(discountRate / 100.0));
    }

    public static Boolean isMembership(User user, Group group) {
        if(user.getIsMembership()) {
            return true;
        }
        if(group instanceof Corporation corporation) {
            return corporation.getIsMembershipSupport();
        }
        return false;
    }

    public static DiscountDto checkMembershipAndGetDiscountDto(User user, Group group, Food food) {
        return (isMembership(user, group)) ? DiscountDto.getDiscount(food) : DiscountDto.getDiscountWithNoMembership(food);
    }


}
