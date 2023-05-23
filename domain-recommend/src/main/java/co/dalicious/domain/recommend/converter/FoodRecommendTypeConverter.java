package co.dalicious.domain.recommend.converter;

import co.dalicious.domain.recommend.entity.FoodRecommendType;
import co.dalicious.system.enums.FoodTag;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class FoodRecommendTypeConverter implements AttributeConverter<List<FoodRecommendType>, String> {

    @Override
    public String convertToDatabaseColumn(List<FoodRecommendType> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (FoodRecommendType frt : attribute) {
            sb.append("(")
                    .append(frt.getFoodTag().getCode())
                    .append(",")
                    .append(frt.getImportance())
                    .append("),");
        }

        sb.setLength(sb.length() - 1);  // to remove the last comma
        return sb.toString();
    }

    @Override
    public List<FoodRecommendType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        String[] entries = dbData.split("\\),\\(");
        List<FoodRecommendType> resultList = new ArrayList<>();
        for (String entry : entries) {
            String cleanEntry = entry.replace("(", "").replace(")", ""); // Remove parentheses
            String[] parts = cleanEntry.split(",");
            FoodTag foodTag = FoodTag.ofCode(Integer.valueOf(parts[0].trim()));  // Adjust this according to your FoodTag constructor
            int importance = Integer.parseInt(parts[1].trim());
            resultList.add(new FoodRecommendType(foodTag, importance));
        }

        return resultList;
    }
}
