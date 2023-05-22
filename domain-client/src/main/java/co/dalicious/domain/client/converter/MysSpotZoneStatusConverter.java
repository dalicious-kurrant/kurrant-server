package co.dalicious.domain.client.converter;

import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MysSpotZoneStatusConverter implements AttributeConverter<MySpotZoneStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MySpotZoneStatus status) {
        return status.getCode();
    }

    @Override
    public MySpotZoneStatus convertToEntityAttribute(Integer dbData) {
        return MySpotZoneStatus.ofCode(dbData);
    }
}
