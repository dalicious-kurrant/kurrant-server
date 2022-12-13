package co.dalicious.system.util;

import java.util.Random;

public class GenerateRandomNumber {
    public static String create8DigitKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public static String create4DigitKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 4; i++) {
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }
}
