package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.RequestedMySpotDto;
import co.dalicious.domain.application_form.dto.mySpotZone.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface RequestedMySpotMapper {
    default RequestedMySpot toEntity(BigInteger userId, RequestedMySpotZones requestedMySpotZones, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException, ParseException {

        CreateAddressRequestDto addressRequestDto = mySpotZoneApplicationFormRequestDto.getAddress();

        if(addressRequestDto.getAddress1() == null) addressRequestDto.setAddress1(mySpotZoneApplicationFormRequestDto.getAddress().getAddress3());

        Address address = new Address(mySpotZoneApplicationFormRequestDto.getAddress());

        return RequestedMySpot.builder()
                .userId(userId)
                .address(address)
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName() == null ? mySpotZoneApplicationFormRequestDto.getAddress().getAddress1() : mySpotZoneApplicationFormRequestDto.getMySpotName())
                .memo(mySpotZoneApplicationFormRequestDto.getMemo())
                .requestedMySpotZones(requestedMySpotZones)
                .build();
    }

    default RequestedMySpotDto toRequestedMySpotDto(RequestedMySpot requestedMySpot) {
        RequestedMySpotDto dto = new RequestedMySpotDto();

        if(requestedMySpot == null) {
            dto.setIsRequested(false);
            return dto;
        }

        dto.setIsRequested(true);
        dto.setAddress(requestedMySpot.getAddress().addressToString());
        return dto;
    };
}
