package co.kurrant.app.public_api;

import co.dalicious.data.redis.RedisUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

class PublicApplicationTests {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private Pbkdf2PasswordEncoder passwordEncoder;
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

	@Test
	void TestPasswordEncoder() {
		String password = "alselalsel1";
		String hashedPassword = passwordEncoder.encode(password);

		System.out.println(hashedPassword);
	}

}
