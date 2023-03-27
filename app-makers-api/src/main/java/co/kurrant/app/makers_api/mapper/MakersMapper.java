package co.kurrant.app.makers_api.mapper;

import co.dalicious.domain.file.dto.ImageWithEnumResponseDto;
import co.dalicious.domain.file.entity.embeddable.ImageWithEnum;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MakersMapper {

    @Mapping(source = "diningTypes", target = "diningTypes")
    @Mapping(source = "makers.address.location", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "makers.address.address2", target = "address2")
    @Mapping(source = "makers.address.address1", target = "address1")
    @Mapping(source = "makers.address.zipCode", target = "zipCode")
    @Mapping(source = "makers.updatedDateTime", target = "updatedDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "makers.createdDateTime", target = "createdDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "makers.accountNumber", target = "accountNumber")
    @Mapping(source = "makers.depositHolder", target = "depositHolder")
    @Mapping(source = "makers.bank", target = "bank")
    @Mapping(source = "makers.closeTime", target = "closeTime")
    @Mapping(source = "makers.openTime", target = "openTime")
    @Mapping(source = "makers.isNutritionInformation", target = "isNutritionInformation")
    @Mapping(source = "makers.contractEndDate", target = "contractEndDate")
    @Mapping(source = "makers.contractStartDate", target = "contractStartDate")
    @Mapping(source = "makers.companyRegistrationNumber", target = "companyRegistrationNumber")
    @Mapping(source = "makers.parentCompanyId", target = "parentCompanyId")
    @Mapping(source = "makers.isParentCompany", target = "isParentCompany")
    @Mapping(source = "makers.serviceForm", target = "serviceForm", qualifiedByName = "generatedServiceForm")
    @Mapping(source = "makers.serviceType", target = "serviceType", qualifiedByName = "generatedServiceType")
    @Mapping(source = "dailyCapacity", target = "dailyCapacity")
    @Mapping(source = "makers.managerPhone", target = "managerPhone")
    @Mapping(source = "makers.managerName", target = "managerName")
    @Mapping(source = "makers.CEOPhone", target = "ceoPhone")
    @Mapping(source = "makers.CEO", target = "ceo")
    @Mapping(source = "makers.companyName", target = "companyName")
    @Mapping(source = "makers.name", target = "name")
    @Mapping(source = "makers.code", target = "code")
    @Mapping(source = "makers.id", target = "id")
    MakersInfoResponseDto toDto(Makers makers, Integer dailyCapacity, List<String> diningTypes);

    OriginDto.WithId originToDto(Origin origin);
    default Origin dtoToOrigin(OriginDto originDto, Makers makers) {
        return new Origin(originDto.getName(), originDto.getFrom(), makers);
    };

    default List<OriginDto.WithId> originToDtos(List<Origin> origins) {
        return origins.stream()
                .map(this::originToDto)
                .toList();
    }

    @Mapping(source = "imageType.code", target = "imageType")
    ImageWithEnumResponseDto imageWithEnumToDto(ImageWithEnum imageWithEnum);

    default List<ImageWithEnumResponseDto> imageWithEnumToDtos(List<ImageWithEnum> imageWithEnums) {
        return imageWithEnums.stream()
                .map(this::imageWithEnumToDto)
                .toList();
    }

    @Named("generatedServiceForm")
    default String generatedServiceForm(ServiceForm serviceForm){
        return serviceForm.getServiceForm();
    }

    @Named("generatedServiceType")
    default String generatedServiceType(ServiceType serviceType){
        return serviceType.getServiceType();
    }

    @Named("TimeFormat")
    default String TimeFormat(Timestamp time){
        return DateUtils.format(time, "yyyy-MM-dd, HH:mm:ss");
    }

    @Named("getLocation")
    default String getLocation(Geometry location){
        if (location == null){
            return null;
        }
        return location.toString();
    }

}
