package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.Country;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CountryConverter implements AttributeConverter<Country, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Country country) {
        return country.getCode();
    }

    @Override
    public Country convertToEntityAttribute(Integer dbData) {
        return Country.ofCode(dbData);
    }
}
