package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.entity.OrderDailyFood;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.payment.entity.CreditCardInfo;

import co.dalicious.domain.user.converter.RefundPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PaymentCancleHistoryMapper {

    @Mapping(source = "cancelReason", target = "cancelReason")
    @Mapping(source = "creditCardInfo", target = "creditCardInfo")
    PaymentCancelHistory toEntity(String cancelReason, RefundPriceDto refundPriceDto, OrderItemDailyFood orderDailyItemFood, String checkOutUrl, String orderCode, BigDecimal refundablePrice, CreditCardInfo creditCardInfo);

}
