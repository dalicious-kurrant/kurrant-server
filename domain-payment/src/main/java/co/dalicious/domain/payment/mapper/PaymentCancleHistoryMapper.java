package co.dalicious.domain.payment.mapper;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.PaymentCancelHistory;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface PaymentCancleHistoryMapper {

    @Mapping(source = "cancelReason", target = "cancelReason")
    @Mapping(source = "cancelAmount", target = "cancelPrice")
    @Mapping(source = "orderItem", target = "orderItem")
    @Mapping(source = "creditCardInfo", target = "creditCardInfo")
    PaymentCancelHistory toEntity(String cancelReason, Integer cancelAmount, OrderItem orderItem, String checkOutUrl, String orderCode, Integer refundablePrice, CreditCardInfo creditCardInfo);

}
