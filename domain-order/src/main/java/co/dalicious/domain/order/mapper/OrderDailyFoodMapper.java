package co.dalicious.domain.order.mapper;

import co.dalicious.domain.client.entity.Spot;
import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface OrderDailyFoodMapper {
    @Mapping(source = "orderCode", target = "code")
    @Mapping(source = "spot.address", target = "address")
    @Mapping(target = "paymentType", constant = "CREDIT_CARD") // TODO: 결제타입 넣기 로직 필요
    @Mapping(source = "user", target = "user")
    @Mapping(source = "spot.group.name", target = "groupName")
    @Mapping(source = "spot.name", target = "spotName")
    @Mapping(source = "spot", target = "spot")
    @Mapping(target = "orderType", constant = "DAILYFOOD")
    OrderDailyFood toEntity(User user, Spot spot, String orderCode);
}
