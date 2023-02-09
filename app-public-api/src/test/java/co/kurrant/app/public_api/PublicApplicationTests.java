package co.kurrant.app.public_api;

import co.dalicious.data.redis.CertificationHash;
import co.dalicious.data.redis.CertificationHashRepository;
import co.dalicious.data.redis.RedisUtil;
import co.dalicious.domain.user.entity.Role;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;


class PublicApplicationTests {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CertificationHashRepository certificationHashRepository;


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
				.build();

//		List<Provider> providers = new ArrayList<>();
//		providers.add(Provider.GENERAL);
//		providers.add(Provider.KAKAO);
//
//		user.setProvider(providers);
		User savedUser = userRepository.save(user);

		Assertions.assertEquals("qwe38280@naver.com", savedUser.getEmail());
	}

	@Test
	void Redis_Repository_Test() {
		CertificationHash certificationHash = CertificationHash.builder()
				.type("1")
				.certificationNumber("0101010101")
				.isAuthenticated(false)
				.build();

	}

}
