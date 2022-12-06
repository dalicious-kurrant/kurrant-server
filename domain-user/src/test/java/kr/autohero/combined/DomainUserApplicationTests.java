package kr.autohero.combined;


import co.dalicious.domain.user.entity.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DomainUserApplicationTests {
    @Test
    public void Provider_test() {
        String sns = "kakao";
        System.out.println(Arrays.toString(Provider.values()));
        Assertions.assertTrue(Arrays.toString(Provider.values()).contains("KAKAO"));
        Provider provider = Provider.KAKAO;
        Provider provider1 = Provider.valueOf(sns.toUpperCase());
        Assertions.assertEquals(provider1, provider);
    }
}
