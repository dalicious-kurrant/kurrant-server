package co.kurrant.app.public_api.service;

import co.dalicious.data.redis.repository.RefreshTokenRepository;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @After
    public void tearDown() throws Exception {
        refreshTokenRepository.deleteAll();
    }

    @Test
    public void 기본_등록_조회기능() {
        //given

    }
}
