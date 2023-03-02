package co.dalicious.system.util;

import co.dalicious.system.enums.DiningType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiningTypesUtils {
    public static List<DiningType> stringToDiningTypes(String strDiningTypes) {
        if (strDiningTypes == null || strDiningTypes.isEmpty()) {
            return null;
        }
        String[] diningTypeStrings = strDiningTypes.split(",");
        List<DiningType> diningTypes = new ArrayList<>();

        for(String diningTypeString : diningTypeStrings) {
            diningTypes.add(DiningType.ofString(diningTypeString.trim()));
        }
        return diningTypes;
    }
}
