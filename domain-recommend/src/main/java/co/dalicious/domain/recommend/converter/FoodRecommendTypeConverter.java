package co.dalicious.domain.recommend.converter;

import co.dalicious.domain.recommend.entity.FoodRecommendType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.List;


@Converter
public class FoodRecommendTypeConverter implements AttributeConverter<List<FoodRecommendType>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<FoodRecommendType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        try {
            return mapper.writeValueAsString(attribute);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert List<FoodRecommendType> to String", e);
        }
    }

    @Override
    public List<FoodRecommendType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().length() == 0) {
            return null;
        }

        try {
            return mapper.readValue(dbData, new TypeReference<List<FoodRecommendType>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
