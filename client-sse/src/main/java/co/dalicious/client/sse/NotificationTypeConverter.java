package co.dalicious.client.sse;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class NotificationTypeConverter implements AttributeConverter<NotificationType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(NotificationType notificationType) {
        return notificationType.getCode();
    }

    @Override
    public NotificationType convertToEntityAttribute(Integer dbData) {
        return NotificationType.ofCode(dbData);
    }
}
