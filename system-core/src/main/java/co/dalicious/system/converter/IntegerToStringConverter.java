package co.dalicious.system.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class IntegerToStringConverter implements AttributeConverter<List<Integer>, String> {
  @Override
  public String convertToDatabaseColumn(List<Integer> attribute) {
    return attribute == null ? null : attribute.stream()
            .map(Object::toString)
            .collect(Collectors.joining(","));
  }

  @Override
  public List<Integer> convertToEntityAttribute(String dbData) {
    return dbData == null || dbData.isEmpty() ? Collections.emptyList() :
            Arrays.stream(dbData.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
  }
}