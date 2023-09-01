package kr.autohero.combined;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class DomainFileApplicationTests {

    @Test
    void contextLoads() {
        String location =
                "https://corretto-dev.s3.ap-northeast-2.amazonaws.com/0001664610488058/power.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20221001T074808Z&X-Amz-SignedHeaders=host&X-Amz-Expires=0&X-Amz-Credential=AKIAJNTAJ3BRPVBJYOKQ%2F20221001%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=d0641a228ff02cb8a83faf450416e9c5345cacbb49313e8dd01be5901cea113d";
        String regex = "https://corretto-dev.s3.ap-northeast-2.amazonaws.com/";
        String result = location.replace(regex, "").replaceAll("\\?.*", "");
        assertEquals(result, "0001664610488058/power.pdf");
    }

    @Test
    void test1() {
        String originalString = "food/0001677115323891/마늘밥.jpg";
        String prefix = "food/0001677115323891/";

        String[] str = originalString.split("\\/");
        String prefixFromOrigin = str[0] + "/" + str[1] + "/";
        assertEquals(prefix, prefixFromOrigin);
        System.out.println("str = " + Arrays.toString(str));
    }

    @Test
    void test2() {
        String location = "https://kurrant-v1-dev.s3.ap-northeast-2.amazonaws.com/food/0001680156885723/KakaoTalk_Photo_2023-03-30-15-13-44%20%281%29.jpeg";
        String location2 = "https://kurrant-v1-dev.s3.ap-northeast-2.amazonaws.com/paycheck/corporations/96/202308/0001693458507112/%EB%B7%B0%ED%8B%B0%EC%85%80%EB%A0%89%EC%85%98%20%28%EC%9D%80%ED%98%9C%EB%B9%8C%EB%94%A9%29%20%EA%B1%B0%EB%9E%98%EB%AA%85%EC%84%B8%EC%84%9C_2023-08.xlsx";

        String key = "food/0001680156885723/KakaoTalk_Photo_2023-03-30-15-13-44%20%281%29.jpeg";
        String key2 = "paycheck/corporations/96/202308/0001693458507112/%EB%B7%B0%ED%8B%B0%EC%85%80%EB%A0%89%EC%85%98%20%28%EC%9D%80%ED%98%9C%EB%B9%8C%EB%94%A9%29%20%EA%B1%B0%EB%9E%98%EB%AA%85%EC%84%B8%EC%84%9C_2023-08.xlsx";

        String prefix = "food/0001680156885723";
        String prefix2 = "paycheck/corporations/96/202308/0001693458507112";

        String[] str = key.split("/");
        String result = String.join("/", Arrays.copyOf(str, str.length - 1));

        String[] str2 = key2.split("/");
        String result2 = String.join("/", Arrays.copyOf(str2, str2.length - 1));

        assertEquals(prefix, result);
        assertEquals(prefix2, result2);
    }
}
