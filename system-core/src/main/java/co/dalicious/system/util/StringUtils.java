package co.dalicious.system.util;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        str = str.replaceAll("[\\[\\]]", ""); // remove opening and closing brackets
        String[] parts = str.split(","); // split comma-separated values
        List<Integer> result = new ArrayList<>();
        for (String part : parts) {
            result.add(Integer.parseInt(part.trim())); // trim and convert to BigInteger
        }
        return result;
    }
}
