package co.dalicious.domain.user.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.user.entity.MySpot;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientType;
import org.mapstruct.Mapper;

import org.locationtech.jts.io.ParseException;

@Mapper(componentModel = "spring")
public interface MySpotMapper {

    default MySpot toMySpot(User user, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException {

        Address address = new Address(mySpotZoneApplicationFormRequestDto.getAddress());

        return MySpot.builder()
                .address(address)
                .clientType(ClientType.MY_SPOT)
                .user(user)
                .isDefault(false)
                .ho(mySpotZoneApplicationFormRequestDto.getHo())
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName())
                .memo(mySpotZoneApplicationFormRequestDto.getMemo())
                .build();
    }
}
