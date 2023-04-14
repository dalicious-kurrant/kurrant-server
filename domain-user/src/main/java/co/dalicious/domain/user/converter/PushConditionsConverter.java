package co.dalicious.domain.user.converter;

import co.dalicious.domain.user.entity.enums.PushCondition;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class PushConditionsConverter implements AttributeConverter<List<PushCondition>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<PushCondition> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (PushCondition condition : attribute) {
            sb.append(condition.getCode()).append(SEPARATOR);
        }

        // remove the last separator
        sb.setLength(sb.length() - SEPARATOR.length());

        return sb.toString();
    }

    @Override
    public List<PushCondition> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String[] pushConditionStr = dbData.split(SEPARATOR);
        List<PushCondition> pushConditions = new ArrayList<>();

        for(String pushConditionString : pushConditionStr) {
            pushConditions.add(PushCondition.ofCode(Integer.parseInt(pushConditionString)));
        }
        return pushConditions;
    }
}
