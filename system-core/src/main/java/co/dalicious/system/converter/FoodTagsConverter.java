package co.dalicious.system.converter;

import co.dalicious.system.enums.FoodTag;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class FoodTagsConverter implements AttributeConverter<List<FoodTag>, String> {
    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<FoodTag> foodTags) {
        if (foodTags == null || foodTags.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (FoodTag foodTag : foodTags) {
            sb.append(foodTag.getCode()).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<FoodTag> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] foodTagStrings = dbData.split(SEPARATOR);
        List<FoodTag> foodTags = new ArrayList<>();

        for(String foodTagString : foodTagStrings) {
            foodTags.add(FoodTag.ofCode(Integer.parseInt(foodTagString)));
        }
        return foodTags;
    }
}
