package co.dalicious.domain.application_form.converter;

import co.dalicious.domain.application_form.entity.enums.ShareSpotRequestType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ShareSpotRequestTypeConverter implements AttributeConverter<ShareSpotRequestType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ShareSpotRequestType attribute) {
        return attribute.getCode();
    }

    @Override
    public ShareSpotRequestType convertToEntityAttribute(Integer dbData) {
        return ShareSpotRequestType.ofCode(dbData);
    }
}
