package co.dalicious.domain.payment.util;

import java.util.Random;

public class TossUtil {

    public static String CreateCustomerKey() {
        int leftLimit = 48; // 숫자 '0'
        int rightLimit = 122; // 영소문자 'z'
        int targetStringLength = 50; // 길이제한
        Random random = new Random();

        String generatedString = random.ints(leftLimit,rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
