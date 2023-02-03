package co.dalicious.domain.order.converter;

import co.dalicious.domain.order.entity.enums.MonetaryStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MonetaryStatusConverter implements AttributeConverter<MonetaryStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MonetaryStatus monetaryStatus) {
        return monetaryStatus.getCode();
    }

    @Override
    public MonetaryStatus convertToEntityAttribute(Integer dbData) {
        return MonetaryStatus.ofCode(dbData);
    }
}
