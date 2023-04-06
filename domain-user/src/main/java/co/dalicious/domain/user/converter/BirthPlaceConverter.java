package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.BirthPlace;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BirthPlaceConverter implements AttributeConverter<BirthPlace, Integer> {

    @Override
    public Integer convertToDatabaseColumn(BirthPlace birthPlace) {
        return birthPlace.getCode() ;
    }

    @Override
    public BirthPlace convertToEntityAttribute(Integer dbData) {
        return BirthPlace.ofCode(dbData);
    }

}
