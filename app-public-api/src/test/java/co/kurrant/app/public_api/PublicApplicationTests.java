package co.kurrant.app.public_api;

import co.dalicious.data.redis.RedisUtil;
import co.dalicious.domain.user.entity.Provider;
import co.dalicious.domain.user.entity.Role;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PublicApplicationTests {
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private UserRepository userRepository;

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
	@Transactional
	void ElementCollection_Provider_test() {
		User user = User.builder()
				.email("qwe38280@naver.com")
				.password("123123")
				.role(Role.USER)
				.name("민지")
				.apartment(null)
				.corporation(null)
				.build();

//		List<Provider> providers = new ArrayList<>();
//		providers.add(Provider.GENERAL);
//		providers.add(Provider.KAKAO);
//
//		user.setProvider(providers);
		User savedUser = userRepository.save(user);

		Assertions.assertEquals("qwe38280@naver.com", savedUser.getEmail());
	}

}
