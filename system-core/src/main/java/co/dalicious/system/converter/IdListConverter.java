package co.dalicious.system.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IdListConverter implements AttributeConverter<List<BigInteger>, String> {
    private static final String SEPARATOR = ",";
    @Override
    public String convertToDatabaseColumn(List<BigInteger> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        return attribute.stream().map(BigInteger::toString).collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public List<BigInteger> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(BigInteger::new)
                .collect(Collectors.toList());
    }
}
