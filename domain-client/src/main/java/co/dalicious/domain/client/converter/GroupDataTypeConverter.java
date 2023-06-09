package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.GroupDataType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GroupDataTypeConverter implements AttributeConverter<GroupDataType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(GroupDataType attribute) {
        return attribute.getCode();
    }

    @Override
    public GroupDataType convertToEntityAttribute(Integer dbData) {
        return GroupDataType.ofCode(dbData);
    }
}
