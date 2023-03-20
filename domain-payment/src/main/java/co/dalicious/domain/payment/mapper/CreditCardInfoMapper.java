package co.dalicious.domain.payment.mapper;

import co.dalicious.domain.payment.dto.CreditCardResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface CreditCardInfoMapper {

    @Mapping(source = "creditCardInfo.cardNumber", target = "cardNumber")
    @Mapping(source = "creditCardInfo.cardCompany", target = "cardCompany")
    @Mapping(source = "creditCardInfo.ownerType", target = "ownerType")
    @Mapping(source = "creditCardInfo.cardType", target = "cardType")
    @Mapping(source = "creditCardInfo.defaultType", target = "defaultType")
    CreditCardResponseDto toDto(CreditCardInfo creditCardInfo);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "defaultType", target = "defaultType")
    @Mapping(source = "cardNumber", target = "cardNumber")
    @Mapping(source = "cardCompany", target = "cardCompany")
    @Mapping(source = "niceCustomerKey", target = "customerKey")
    @Mapping(source = "billingKey", target = "niceBillingKey")
    @Mapping(source = "id", target = "user.id")
    CreditCardInfo toEntity(String cardNumber, String cardCompany, String niceCustomerKey, String billingKey, BigInteger id, Integer defaultType, Integer status);


}
