package co.kurrant.app.public_api;

import co.dalicious.data.redis.entity.CertificationHash;
import co.dalicious.data.redis.repository.CertificationHashRepository;
import co.dalicious.data.redis.RedisUtil;
import co.dalicious.domain.user.entity.enums.Role;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.dalicious.system.util.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;
import java.time.LocalDate;


class PublicApplicationTests {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CertificationHashRepository certificationHashRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;


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

	@Test
	void Test_LocalDate_Formatter() {
		LocalDate now = LocalDate.now();
		String formattedDate = DateUtils.format(now, "yyyy-MM-dd");
		System.out.println(formattedDate);
	}

	@Test
	void Test_LocalDate_Until_Function() {
		LocalDate now = LocalDate.now();
		LocalDate christMas = LocalDate.of(2022, 12, 25);
		System.out.println(now.until(christMas).getDays());
	}

}
