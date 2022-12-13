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

    public static String idToString(Integer id) {
        String strId = id.toString();
        if(id > 10000) {
            return strId.substring(0, 4);
        } else if (id > 1000) {
            return strId;
        } else if (id > 100) {
            return "0" + strId;
        } else if (id > 10) {
            return "00" + strId;
        } else {
            return "000" + strId;
        }
    }
}
