import co.dalicious.domain.user.dto.UserDto;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MembershipJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRepository membershipRepository;


    @Before
    public void setUp() {
        User user1 = User.builder()
                .name("테스트1")
                .email("qwe123@naver.com")
                .build();
        User user2 = User.builder()
                .name("테스트2")
                .email("qwe123@gmail.com")
                .build();
        User user3 = User.builder()
                .name("테스트2")
                .email("qwe123@kakao.com")
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        membershipRepository.save(new Membership(MembershipStatus.PROCESSING, MembershipSubscriptionType.MONTH, LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), true, user1));
        membershipRepository.save(new Membership(MembershipStatus.PROCESSING, MembershipSubscriptionType.MONTH, LocalDate.now().minusDays(20), LocalDate.now().plusDays(10), true, user2));
        membershipRepository.save(new Membership(MembershipStatus.PROCESSING, MembershipSubscriptionType.MONTH, LocalDate.now().plusDays(10), LocalDate.now().plusDays(20), true, user3));
    }

    @Test
    public void testMembershipCheckJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());

        User user1 = userRepository.findByName("user1");
        User user2 = userRepository.findByName("user2");
        User user3 = userRepository.findByName("user3");

        assertTrue(user1.getIsMembership());
        assertFalse(user2.getIsMembership());
        assertEquals(true, user3.getIsMembership());
    }
}

