package co.dalicious.integration.client.user.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.user.entity.User;
import co.dalicious.integration.client.user.entity.MySpot;
import org.mapstruct.Mapper;

import org.locationtech.jts.io.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MySpotMapper {
    default MySpot toMySpot(User user, MySpotZone mySpotZone, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException {

        CreateAddressRequestDto addressRequestDto = mySpotZoneApplicationFormRequestDto.getAddress();

        if (addressRequestDto.getAddress1() == null) {
            addressRequestDto.setAddress1(mySpotZoneApplicationFormRequestDto.getJibunAddress());
        }

        Address address = new Address(mySpotZoneApplicationFormRequestDto.getAddress());

        return MySpot.builder()
                .address(address)
                .diningTypes(mySpotZone.getDiningTypes())
                .userId(user.getId())
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName())
                .isDelete(false)
                .build();
    }

    @Mapping(source = "requestedMySpot.name", target = "name")
    @Mapping(source = "requestedMySpot.address", target = "address")
    @Mapping(source = "requestedMySpot.memo", target = "memo")
    @Mapping(source = "MySpotZone.diningTypes", target = "diningTypes")
    MySpot toEntity(RequestedMySpot requestedMySpot, Group MySpotZone) throws ParseException;

    default List<MySpot> toEntityList(MySpotZone mySpotZone, List<RequestedMySpot> requestedMySpots){
        return new ArrayList<>(requestedMySpots.stream().map(v -> {
            try {
                return toEntity(v, mySpotZone);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).toList());
    }
}
