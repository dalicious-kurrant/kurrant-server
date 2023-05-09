package co.dalicious.domain.banner.converter;

import co.dalicious.domain.banner.enums.BannerType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BannerTypeConverter implements AttributeConverter<BannerType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BannerType attribute) {
        return attribute.getCode();
    }

    @Override
    public BannerType convertToEntityAttribute(Integer dbData) {
        return BannerType.ofCode(dbData);
    }
}
