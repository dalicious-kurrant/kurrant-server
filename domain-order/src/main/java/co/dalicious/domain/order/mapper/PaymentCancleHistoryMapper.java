package co.dalicious.domain.order.mapper;

import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.payment.entity.CreditCardInfo;

import co.dalicious.domain.user.converter.RefundPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PaymentCancleHistoryMapper {
    @Mapping(target = "cancelDateTime", expression = "java(LocalDateTime.now())")
    @Mapping(source = "cancelReason", target = "cancelReason")
    @Mapping(source = "refundPriceDto.point", target = "refundPointPrice")
    @Mapping(source = "refundPriceDto.price", target = "cancelPrice")
    @Mapping(source = "refundPriceDto.deliveryFee", target = "refundDeliveryFee")
    @Mapping(source = "refundablePrice", target = "refundablePrice")
    @Mapping(source = "checkOutUrl", target = "checkOutUrl")
    @Mapping(source = "orderCode", target = "orderCode")
    @Mapping(source = "orderDailyItemFood.order", target = "order")
    @Mapping(source = "orderDailyItemFood", target = "orderItem")
    @Mapping(source = "creditCardInfo", target = "creditCardInfo")
    PaymentCancelHistory orderDailyItemFoodToEntity(String cancelReason, RefundPriceDto refundPriceDto, OrderItemDailyFood orderDailyItemFood, String checkOutUrl, String orderCode, BigDecimal refundablePrice, CreditCardInfo creditCardInfo);

    @Mapping(target = "cancelDateTime", expression = "java(LocalDateTime.now())")
    @Mapping(source = "cancelReason", target = "cancelReason")
    @Mapping(target = "refundPointPrice", expression = "java(BigDecimal.ZERO)")
    @Mapping(source = "refundPrice", target = "cancelPrice")
    @Mapping(target = "refundDeliveryFee", expression = "java(BigDecimal.ZERO)")
    @Mapping(source = "refundablePrice", target = "refundablePrice")
    @Mapping(source = "checkOutUrl", target = "checkOutUrl")
    @Mapping(source = "orderCode", target = "orderCode")
    @Mapping(source = "orderItemMembership.order", target = "order")
    @Mapping(source = "orderItemMembership", target = "orderItem")
    @Mapping(source = "creditCardInfo", target = "creditCardInfo")
    PaymentCancelHistory orderItemMembershipToEntity(String cancelReason, BigDecimal refundPrice, OrderItemMembership orderItemMembership, String checkOutUrl, String orderCode, BigDecimal refundablePrice, CreditCardInfo creditCardInfo);
}
