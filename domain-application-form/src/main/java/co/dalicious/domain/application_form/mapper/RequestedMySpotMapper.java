package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.RequestedMySpotDto;
import co.dalicious.domain.application_form.dto.mySpotZone.MySpotZoneApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.application_form.entity.RequestedMySpotZones;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface RequestedMySpotMapper {
    default RequestedMySpot toEntity(BigInteger userId, RequestedMySpotZones requestedMySpotZones, MySpotZoneApplicationFormRequestDto mySpotZoneApplicationFormRequestDto) throws ParseException {
        Address address = createAddress(mySpotZoneApplicationFormRequestDto.getAddress());

        return RequestedMySpot.builder()
                .userId(userId)
                .address(address)
                .name(mySpotZoneApplicationFormRequestDto.getMySpotName() == null ? address.addressToString() : mySpotZoneApplicationFormRequestDto.getMySpotName())
                .memo(mySpotZoneApplicationFormRequestDto.getMemo())
                .requestedMySpotZones(requestedMySpotZones)
                .phone(mySpotZoneApplicationFormRequestDto.getPhone())
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

    @Mapping(target = "address", expression = "java(createAddress(requestDto.getAddress()))")
    @Mapping(target = "name", expression = "java(requestDto.getMySpotName() == null ? requestDto.getAddress().getAddress2() == null ? requestDto.getAddress().getAddress1() : requestDto.getAddress().getAddress1() + \" \" + requestDto.getAddress().getAddress2() : requestDto.getMySpotName())")
    void updateRequestedMySpot(MySpotZoneApplicationFormRequestDto requestDto, @MappingTarget RequestedMySpot requestedMySpot) throws ParseException;

    default Address createAddress(CreateAddressRequestDto address) throws ParseException {
        if(address.getAddress1() == null) address.setAddress1(address.getAddress3());
        return new Address(address);
    }
}
