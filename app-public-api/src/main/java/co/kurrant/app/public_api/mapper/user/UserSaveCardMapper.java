package co.kurrant.app.public_api.mapper.user;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.kurrant.app.public_api.dto.user.SaveCreditCardRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSaveCardMapper {


    SaveCreditCardRequestDto toDto(CreditCardInfo creditCardInfo);

}
