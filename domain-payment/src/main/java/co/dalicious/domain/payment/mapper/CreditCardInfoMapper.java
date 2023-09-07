package co.dalicious.domain.payment.mapper;

import co.dalicious.domain.payment.dto.CreditCardDto;
import co.dalicious.domain.payment.dto.CreditCardResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PGCompany;
import co.dalicious.domain.user.entity.User;
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

    default CreditCardInfo toEntity(CreditCardDto.Response saveCardResponse, User user, Integer defaultType, PGCompany pgCompany) {
        return CreditCardInfo.builder()
                .status(1)
                .defaultType(defaultType)
                .cardNumber(saveCardResponse.getCardNumber())
                .cardCompany(saveCardResponse.getCardCompany())
                .niceBillingKey(pgCompany.equals(PGCompany.NICE) ? saveCardResponse.getBillingKey() : null)
                .mingleBillingKey(pgCompany.equals(PGCompany.MINGLE) ? saveCardResponse.getBillingKey() : null)
                .tossBillingKey(pgCompany.equals(PGCompany.TOSS) ? saveCardResponse.getBillingKey() : null)
                .user(user)
                .build();
    };

    /* NicePay Response
    @Mapping(target = "status", constant = "1")
    @Mapping(source = "defaultType", target = "defaultType")
    @Mapping(source = "saveCardResponse.cardNumber", target = "cardNumber")
    @Mapping(source = "saveCardResponse.cardCompany", target = "cardCompany")
    @Mapping(source = "saveCardResponse.customerKey", target = "customerKey")
    @Mapping(source = "saveCardResponse.billingKey", target = "niceBillingKey")
    @Mapping(source = "id", target = "user.id")
    CreditCardInfo toEntity(CreditCardDto.Response saveCardResponse, BigInteger id, Integer defaultType);
     */


}
