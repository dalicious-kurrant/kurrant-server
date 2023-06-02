package co.dalicious.domain.application_form.mapper;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.application_form.dto.ApplicationFormDto;
import org.apache.catalina.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigInteger;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationFormDto toApplicationFromDto(BigInteger id, String name, Address address, Integer clientType, Boolean isExist, Boolean isAlarm);
}
