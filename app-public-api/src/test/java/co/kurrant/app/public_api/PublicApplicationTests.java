package co.kurrant.app.public_api;

import co.dalicious.data.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PublicApplicationTests {
	@Autowired
	private RedisUtil redisUtil;
	@Test
	void TestRedisConnection() {
		//given
		String key = "minji";
		String value = "3826";
		long duration = 300 * 1L;

		//when
		redisUtil.setDataExpire(key, value, duration);

		//then
		Assertions.assertTrue(redisUtil.hasKey(key));
	}

}
