package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.mySpotZone.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.MySpot;
import org.mapstruct.Mapper;

import org.locationtech.jts.io.ParseException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MySpotMapper {
    default MySpot toMySpot(BigInteger userId, MySpotZone mySpotZone, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException {

        CreateAddressRequestDto addressRequestDto = mySpotZoneApplicationFormRequestDto.getAddress();

        if (addressRequestDto.getAddress1() == null) addressRequestDto.setAddress1(mySpotZoneApplicationFormRequestDto.getAddress().getAddress3());

        Address address = new Address(mySpotZoneApplicationFormRequestDto.getAddress());

        return MySpot.builder()
                .address(address)
                .diningTypes(mySpotZone.getDiningTypes())
                .userId(userId)
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName() == null ? address.addressToString() : mySpotZoneApplicationFormRequestDto.getMySpotName())
                .isDelete(false)
                .build();
    }

    @Mapping(source = "requestedMySpot.name", target = "name")
    @Mapping(source = "requestedMySpot.address", target = "address")
    @Mapping(source = "requestedMySpot.memo", target = "memo")
    @Mapping(source = "group.diningTypes", target = "diningTypes")
    @Mapping(source = "group", target = "group")
    @Mapping(source = "requestedMySpot.userId", target = "userId")
    @Mapping(target = "isDelete", defaultValue = "false")
    MySpot toEntity(RequestedMySpot requestedMySpot, Group group) throws ParseException;

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
