package kr.autohero.combined;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DomainFileApplicationTests {

  @Test
  void contextLoads() {
    String location =
        "https://corretto-dev.s3.ap-northeast-2.amazonaws.com/0001664610488058/power.pdf?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20221001T074808Z&X-Amz-SignedHeaders=host&X-Amz-Expires=0&X-Amz-Credential=AKIAJNTAJ3BRPVBJYOKQ%2F20221001%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=d0641a228ff02cb8a83faf450416e9c5345cacbb49313e8dd01be5901cea113d";
    String regex = "https://corretto-dev.s3.ap-northeast-2.amazonaws.com/";
    String result = location.replace(regex, "").replaceAll("\\?.*", "");
    assertEquals(result, "0001664610488058/power.pdf");

  }
}
