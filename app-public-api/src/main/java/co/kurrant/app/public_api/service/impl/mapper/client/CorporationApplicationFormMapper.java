package co.kurrant.app.public_api.service.impl.mapper.client;

import co.dalicious.client.core.mapper.GenericMapper;
import co.dalicious.domain.address.dto.CreateAddressRequestDto;
import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.corporation.CorporationApplicationFormRequestDto;
import co.dalicious.domain.application_form.entity.CorporationApplicationForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;


//@Mapper(componentModel = "spring")
//public interface CorporationApplicationFormMapper extends GenericMapper<CorporationApplicationFormRequestDto, CorporationApplicationForm> {
//    CorporationApplicationFormMapper INSTANCE = Mappers.getMapper(CorporationApplicationFormMapper.class);
//
//    @Mapping(source = "user.name", target = "applierName")
//    @Mapping(source = "user.phone", target = "phone")
//    @Mapping(source = "user.email", target = "email")
//    @Mapping(source = "corporationInfo.corporationName", target = "corporationName")
//    @Mapping(source = "corporationInfo.employeeCount", target = "employeeCount")
//    @Mapping(source = "corporationInfo.startDate", target = "serviceStartDate")
//    @Mapping(source = "address", target = "address", qualifiedByName = "addressDtoToEntity")
//    @Mapping(source = "option.isGarbage", target = "isGarbage")
//    @Mapping(source = "option.isHotStorage", target = "isHotStorage")
//    @Mapping(source = "option.isSetting", target = "isSetting")
//    CorporationApplicationForm toEntity(CorporationApplicationFormRequestDto dto);
//
//    @Named("addressDtoToEntity")
//    default Address addressDtoToEntity(CreateAddressRequestDto dto) {
//        return Address.builder()
//                .createAddressRequestDto(dto)
//                .build();
//    }
//
//}
