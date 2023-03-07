package co.dalicious.system.util;

import org.junit.jupiter.api.Test;

public class StringTest {
    @Test
    public void stringTest() {
        String str = "스파크 플러스 역삼점";
        str = str.replaceAll("\\s+", "");
        String str2 = "스파크플러스";
        System.out.println("str = " + str);
        System.out.println("str2 = " + str2);
        System.out.println(str.replaceAll("\\+s", "").contains(str2));
    }
}
