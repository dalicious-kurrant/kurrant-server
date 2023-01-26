package co.dalicious.domain.payment.mapper;

import co.dalicious.domain.payment.dto.CreditCardResponseDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditCardInfoMapper {

    @Mapping(source = "creditCardInfo.cardNumber", target = "cardNumber")
    @Mapping(source = "creditCardInfo.cardCompany", target = "cardCompany")
    @Mapping(source = "creditCardInfo.ownerType", target = "ownerType")
    @Mapping(source = "creditCardInfo.cardType", target = "cardType")
    @Mapping(source = "creditCardInfo.defaultType", target = "defaultType")
    CreditCardResponseDto toDto(CreditCardInfo creditCardInfo);

}
