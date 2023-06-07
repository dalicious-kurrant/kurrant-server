package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.application_form.dto.share.ShareSpotDto;
import co.dalicious.domain.application_form.entity.RequestedShareSpot;
import co.dalicious.system.util.DateUtils;
import org.locationtech.jts.io.ParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = {DateUtils.class})
public interface RequestedShareSpotMapper {
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(request.getDeliveryTime()))")
    RequestedShareSpot toEntity(ShareSpotDto.Request request) throws ParseException;

    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(request.getDeliveryTime()))")
    @Mapping(target = "shareSpotRequestType", expression = "java(ShareSpotRequestType.ofCode(request.getShareSpotRequestType()))")
    RequestedShareSpot toEntity(ShareSpotDto.AdminRequest request) throws ParseException;

    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "shareSpotRequestType.type", target = "shareSpotRequestType")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.timeToString(requestedShareSpot.getDeliveryTime()))")
    @Mapping(source = "createdDateTime", target = "createdDate", qualifiedByName = "localDateToDate")
    ShareSpotDto.Response toDto(RequestedShareSpot requestedShareSpot);

    @Mapping(target = "shareSpotRequestType", expression = "java(ShareSpotRequestType.ofCode(request.getShareSpotRequestType()))")
    @Mapping(target = "deliveryTime", expression = "java(DateUtils.stringToLocalTime(request.getDeliveryTime()))")
    void updateRequestedShareSpotFromRequest(ShareSpotDto.AdminRequest request, @MappingTarget RequestedShareSpot requestedShareSpot) throws ParseException;

    default List<ShareSpotDto.Response> toDtos(Page<RequestedShareSpot> requestedShareSpots) {
        return requestedShareSpots.stream()
                .map(this::toDto)
                .toList();
    }

    @Named("localDateToDate")
    default String localDateToDate(Timestamp timestamp) {
        return DateUtils.localDateToString(timestamp.toLocalDateTime().toLocalDate());
    }
}
