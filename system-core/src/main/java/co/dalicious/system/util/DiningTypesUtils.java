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

    public static List<DiningType> codesToDiningTypes(List<Integer> codes) {
        if(codes == null) return null;
        List<DiningType> diningTypes = new ArrayList<>();

        for (Integer code : codes) {
            diningTypes.add(DiningType.ofCode(code));
        }
        return diningTypes;
    }

    public static List<Integer> diningTypesToCodes(List<DiningType> diningTypes) {
        return diningTypes.stream().map(DiningType::getCode).toList();
    }

    public static List<DiningType> stringCodeToDiningTypes(String strDiningTypes) {
        if (strDiningTypes == null || strDiningTypes.isEmpty()) {
            return null;
        }
        String[] diningTypeStrings = strDiningTypes.split(",");
        List<DiningType> diningTypes = new ArrayList<>();

        for(String diningTypeString : diningTypeStrings) {
            diningTypes.add(DiningType.ofStringCode(diningTypeString.trim()));
        }
        return diningTypes;
    }
}
