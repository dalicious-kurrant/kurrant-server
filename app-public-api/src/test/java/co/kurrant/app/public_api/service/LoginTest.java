package co.kurrant.app.public_api.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@RequiredArgsConstructor
public class LoginTest {
    private final PasswordEncoder passwordEncoder;

    @Test
    void test() {
        String password = "ekffltutm1!";
        System.out.println(passwordEncoder.encode(password));
    }
}
