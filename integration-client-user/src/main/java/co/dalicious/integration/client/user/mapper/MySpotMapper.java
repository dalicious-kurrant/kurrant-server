package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.integration.client.user.entity.MySpot;
import org.mapstruct.Mapper;

import org.locationtech.jts.io.ParseException;

@Mapper(componentModel = "spring")
public interface MySpotMapper {

    default MySpot toMySpot(User user, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException {

        CreateAddressRequestDto addressRequestDto = mySpotZoneApplicationFormRequestDto.getAddress();

        if(addressRequestDto.getAddress1() == null) {
            addressRequestDto.setAddress1(mySpotZoneApplicationFormRequestDto.getJibunAddress());
        }

        Address address = new Address(mySpotZoneApplicationFormRequestDto.getAddress());

        return MySpot.builder()
                .address(address)
                .clientType(ClientType.MY_SPOT)
                .user(user)
                .isDefault(false)
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName())
                .build();
    }
}
