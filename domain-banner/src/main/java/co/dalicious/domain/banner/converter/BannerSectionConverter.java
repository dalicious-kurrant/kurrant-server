`package co.dalicious.domain.banner.converter;

import co.dalicious.domain.banner.enums.BannerSection;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BannerSectionConverter implements AttributeConverter<BannerSection, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BannerSection attribute) {
        return attribute.getCode();
    }

    @Override
    public BannerSection convertToEntityAttribute(Integer dbData) {
        return BannerSection.ofCode(dbData);
    }
}
