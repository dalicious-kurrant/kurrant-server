package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.entity.FoodCapacity;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDto;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MakersMapper {
    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    MakersDto.Makers makersToDto(Makers makers);

    default List<MakersDto.Makers> makersToDtos(List<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
                .collect(Collectors.toList());
    }


    @Mapping(source = "dinnerCapacity", target = "dinnerCapacity")
    @Mapping(source = "lunchCapacity", target = "lunchCapacity")
    @Mapping(source = "morningCapacity", target = "morningCapacity")
    @Mapping(source = "makers", target = "dinnerLastOrderTime", qualifiedByName = "getDinnerLastOrderTime")
    @Mapping(source = "makers", target = "lunchLastOrderTime", qualifiedByName = "getLunchLastOrderTime")
    @Mapping(source = "makers", target = "morningLastOrderTime", qualifiedByName = "getMorningLastOrderTime")
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
    @Mapping(source = "makers.memo", target = "memo")
    @Mapping(source = "makers.fee", target = "fee")
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
    MakersInfoResponseDto toDto(Makers makers, Integer dailyCapacity, List<String> diningTypes,
                                Integer morningCapacity, Integer lunchCapacity, Integer dinnerCapacity);

    @Named("getDinnerLastOrderTime")
    default String getDinnerLastOrderTime(Makers makers) {
        MakersCapacity dinnerCapacity = makers.getMakersCapacity(DiningType.DINNER);
        if(dinnerCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(dinnerCapacity.getLastOrderTime());
    }

    @Named("getLunchLastOrderTime")
    default String getLunchLastOrderTime(Makers makers) {
        MakersCapacity dinnerCapacity = makers.getMakersCapacity(DiningType.LUNCH);
        if(dinnerCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(dinnerCapacity.getLastOrderTime());
    }

    @Named("getMorningLastOrderTime")
    default String getMorningLastOrderTime(Makers makers) {
        MakersCapacity dinnerCapacity = makers.getMakersCapacity(DiningType.MORNING);
        if(dinnerCapacity == null) return "정보 없음";
        return DayAndTime.dayAndTimeToString(dinnerCapacity.getLastOrderTime());
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
        return location.toString().substring(7,location.toString().length()-1);
    }


    @Mapping(source = "dto.isNutritionInformation", target = "isNutritionInformation")
    @Mapping(source = "dto.closeTime", target = "closeTime", qualifiedByName = "stringToTimeFormat")
    @Mapping(source = "dto.openTime", target = "openTime", qualifiedByName = "stringToTimeFormat")
    @Mapping(source = "dto.contractEndDate", target = "contractEndDate", qualifiedByName = "stringToDateFormat")
    @Mapping(source = "dto.contractStartDate", target = "contractStartDate", qualifiedByName = "stringToDateFormat")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "dto.serviceForm", target = "serviceForm", qualifiedByName = "getServiceForm")
    @Mapping(source = "dto.serviceType", target = "serviceType", qualifiedByName = "getServiceType")
    @Mapping(source = "dto.managerName", target = "managerName")
    @Mapping(source = "dto.ceoPhone", target = "CEOPhone")
    @Mapping(source = "dto.ceo", target = "CEO")
    @Mapping(source = "dto.companyName", target = "companyName")
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.code", target = "code")
    Makers toEntity(SaveMakersRequestDto dto, Address address);

    /*
    @Named("getRole")
    default Role getRole(String role){
        if (role.equals("관리자")){
            return Role.MANAGER;
        }
        return Role.USER;
    }
    */

    @Named("getServiceType")
    default ServiceType getServiceType(String serviceType){
        return ServiceType.ofString(serviceType);
    }

    @Named("getServiceForm")
    default ServiceForm getServiceForm(String serviceForm){
        return ServiceForm.ofString(serviceForm);
    }

    @Named("stringToDateFormat")
    default LocalDate stringToDateFormat(String time){
        return LocalDate.parse(time);
    }

    @Named("stringToTimeFormat")
    default LocalTime stringToTimeFormat(String time){
        return LocalTime.parse(time);
    }

    /*
    @Named("stringToTimeStampFormat")
    default Timestamp stringToTimeStampFormat(String time){
        return Timestamp.valueOf(time);
    }*/


}
