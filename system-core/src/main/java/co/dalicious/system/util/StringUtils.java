package co.dalicious.system.util;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {
    public static String toQueryString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }

    public static List<BigInteger> parseBigIntegerList(String str) {
        str = str.replaceAll("[\\[\\]]", ""); // remove opening and closing brackets
        String[] parts = str.split(","); // split comma-separated values
        List<BigInteger> result = new ArrayList<>();
        for (String part : parts) {
            result.add(new BigInteger(part.trim())); // trim and convert to BigInteger
        }
        return result;
    }

    public static List<Integer> parseIntegerList(String str) {
        if(str == null) return null;
        str = str.replaceAll("[\\[\\]]", ""); // remove opening and closing brackets
        String[] parts = str.split(","); // split comma-separated values
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Integer.parseInt(part.trim())); // trim and convert to BigInteger
        }
        return result;
    }

    public static String StringListToString(List<String> stringList) {
        StringBuilder resultName = new StringBuilder();
        for (String str : stringList) {
            resultName.append(str).append(", ");
        }
        return resultName.substring(0, resultName.length() - 2);
    }

    public static List<String> StringToStringList(String stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(stringList.split(", "));
    }

    public static String integerListToString(List<Integer> integers) {
        return integers == null ? null : integers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public static String BigIntegerListToString(List<BigInteger> integers) {
        return integers == null ? null : integers.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
}
