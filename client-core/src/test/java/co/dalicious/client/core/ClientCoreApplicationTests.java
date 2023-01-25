package co.dalicious.client.core;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class ClientCoreApplicationTests {
    private final PasswordEncoder passwordEncoder;

    ClientCoreApplicationTests(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    void test() {
        String password = "ekffltutm1!";
        System.out.println(passwordEncoder.encode(password));
    }
}
