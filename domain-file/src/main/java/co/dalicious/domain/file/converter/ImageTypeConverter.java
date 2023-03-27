package co.dalicious.domain.file.converter;

import co.dalicious.domain.file.entity.embeddable.enums.ImageType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ImageTypeConverter implements AttributeConverter<ImageType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ImageType attribute) {
        return attribute.getCode();
    }

    @Override
    public ImageType convertToEntityAttribute(Integer dbData) {
        return ImageType.ofCode(dbData);
    }
}
