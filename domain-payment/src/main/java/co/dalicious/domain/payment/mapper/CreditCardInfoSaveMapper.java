package co.dalicious.domain.payment.mapper;

import co.dalicious.domain.payment.dto.CreditCardSaveDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface CreditCardInfoSaveMapper {


    @Mapping(source="id", target = "user", qualifiedByName = "userById")
    CreditCardInfo toSaveEntity(String cardNumber, BigInteger id, String ownerType, String cardType, String customerKey, String billingKey, String cardCompany);

    @Named("userById")
    default User userById(BigInteger userId){
        return User.builder()
                .id(userId).build();
    }
    //String cardNumber, User user, String ownerType, String cardType, String customerKey, String billingKey
}
