package co.dalicious.integration.client.user.converter;

import co.dalicious.integration.client.user.entity.enums.MySpotZoneStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MySpotZoneStatusConverter implements AttributeConverter<MySpotZoneStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MySpotZoneStatus status) {
        return status.getCode();
    }

    @Override
    public MySpotZoneStatus convertToEntityAttribute(Integer dbData) {
        return MySpotZoneStatus.ofCode(dbData);
    }
}
