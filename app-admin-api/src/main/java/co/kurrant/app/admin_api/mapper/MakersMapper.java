package co.kurrant.app.admin_api.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.ServiceForm;
import co.dalicious.domain.food.entity.enums.ServiceType;
import co.dalicious.system.enums.Days;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.DaysUtil;
import co.kurrant.app.admin_api.dto.MakersDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDto;
import org.locationtech.jts.geom.Geometry;
import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {DaysUtil.class, DateUtils.class, DayAndTime.class})
public interface MakersMapper {
    @Mapping(source = "id", target = "makersId")
    @Mapping(source = "name", target = "makersName")
    MakersDto.Makers makersToDto(Makers makers);

    default List<MakersDto.Makers> makersToDtos(Collection<Makers> makers) {
        return makers.stream()
                .map(this::makersToDto)
                .collect(Collectors.toList());
    }

    @Mapping(source = "address.location", target = "location", qualifiedByName = "getLocation")
    @Mapping(source = "address.address2", target = "address2")
    @Mapping(source = "address.address1", target = "address1")
    @Mapping(source = "address.zipCode", target = "zipCode")
    @Mapping(source = "updatedDateTime", target = "updatedDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "createdDateTime", target = "createdDateTime", qualifiedByName = "TimeFormat")
    @Mapping(source = "serviceForm", target = "serviceForm", qualifiedByName = "generatedServiceForm")
    @Mapping(source = "serviceType", target = "serviceType", qualifiedByName = "generatedServiceType")
    @Mapping(source = "CEOPhone", target = "ceoPhone")
    @Mapping(source = "CEO", target = "ceo")
    @Mapping(source = "serviceDays", target = "serviceDays", qualifiedByName = "daysToString")
    MakersInfoResponseDto toDto(Makers makers);

    @AfterMapping
    default void afterMapping(@MappingTarget MakersInfoResponseDto dto, Makers makers) {
        dto.setDailyCapacity(makers.getDailyCapacity());
        dto.setDiningTypes(makers.getDiningTypes().stream().map(DiningType::getDiningType).toList());
        toMakersCapacityDto(dto, makers);
    }
    @Named("toMakersCapacityDto")
    default void toMakersCapacityDto(MakersInfoResponseDto dto, Makers makers) {
        MakersCapacity morningCapacity = makers.getMakersCapacity(DiningType.MORNING);
        MakersCapacity lunchCapacity = makers.getMakersCapacity(DiningType.LUNCH);
        MakersCapacity dinnerCapacity = makers.getMakersCapacity(DiningType.DINNER);

        if(morningCapacity != null) {
            dto.setMorningCapacity(morningCapacity.getCapacity());
            dto.setMorningLastOrderTime(DayAndTime.dayAndTimeToString(morningCapacity.getLastOrderTime()));
            dto.setMorningMinTime(DateUtils.timeToString(morningCapacity.getMinTime()));
            dto.setMorningMaxTime(DateUtils.timeToString(morningCapacity.getMaxTime()));
        }
        if(lunchCapacity != null) {
            dto.setLunchCapacity(lunchCapacity.getCapacity());
            dto.setLunchLastOrderTime(DayAndTime.dayAndTimeToString(lunchCapacity.getLastOrderTime()));
            dto.setLunchMinTime(DateUtils.timeToString(lunchCapacity.getMinTime()));
            dto.setLunchMaxTime(DateUtils.timeToString(lunchCapacity.getMaxTime()));
        }
        if(dinnerCapacity != null) {
            dto.setDinnerCapacity(dinnerCapacity.getCapacity());
            dto.setDinnerLastOrderTime(DayAndTime.dayAndTimeToString(dinnerCapacity.getLastOrderTime()));
            dto.setDinnerMinTime(DateUtils.timeToString(dinnerCapacity.getMinTime()));
            dto.setDinnerMaxTime(DateUtils.timeToString(dinnerCapacity.getMaxTime()));
        }
    }

    @Named("daysToString")
    default String daysToString(List<Days> days) {
        return DaysUtil.serviceDaysToDaysString(days);
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
    @Mapping(source = "dto.isActive", target = "isActive")
    @Mapping(target = "serviceDays", expression = "java(dto.getServiceDays() == null ? null : DaysUtil.serviceDaysToDaysList(dto.getServiceDays()))")
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
