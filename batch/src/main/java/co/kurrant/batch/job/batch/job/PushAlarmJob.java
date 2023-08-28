package co.kurrant.batch.job.batch.job;

import co.dalicious.client.alarm.dto.BatchAlarmDto;
import co.dalicious.client.alarm.dto.PushRequestDtoByUser;
import co.dalicious.client.alarm.entity.enums.AlarmType;
import co.dalicious.client.alarm.service.PushService;
import co.dalicious.client.alarm.util.PushUtil;
import co.dalicious.data.redis.dto.SseReceiverDto;
import co.dalicious.domain.client.entity.MySpotZone;
import co.dalicious.domain.client.entity.enums.GroupDataType;
import co.dalicious.domain.client.entity.enums.MySpotZoneStatus;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.service.PushAlarmService;
import exception.ApiException;
import exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PushAlarmJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final PushAlarmService pushAlarmService;
    private final PushUtil pushUtil;
    private final PushService pushService;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final int CHUNK_SIZE = 500;

    @Bean(name = "pushAlarmJob1")
    public Job pushAlarmJob1() {
        return jobBuilderFactory.get("pushAlarmJob1")
                .start(pushAlarmJob_step())
                .build();
    }

    @Bean(name = "pushAlarmJob2")
    public Job pushAlarmJob2() {
        return jobBuilderFactory.get("pushAlarmJob2")
                .start(pushAlarmJob2_step1())
                .next(pushAlarmJob2_step2())
                .next(pushAlarmJob2_step3())
                .build();
    }

    @Bean(name = "pushAlarmJob3")
    public Job pushAlarmJob3() {
        return jobBuilderFactory.get("pushAlarmJob3")
                .start(pushAlarmJob3_step1())
                .build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob_step() {
        return stepBuilderFactory.get("pushAlarmJob_step")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(lastOrderTimePushAlarmReader())
                .processor(lastOrderTimePushAlarmProcessor())
                .writer(lastOrderTimePushAlarmWriter())
                .faultTolerant()
                .skip(ApiException.class) // Add the exception classes you want to skip
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob2_step1() {
        return stepBuilderFactory.get("pushAlarmJob2_step1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[my spot zone 찾기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT msz FROM MySpotZone msz WHERE msz.openDate = :currentDate and msz.mySpotZoneStatus != 1";
                    final LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
                    final TypedQuery<MySpotZone> query = entityManager.createQuery(queryString, MySpotZone.class);
                    query.setParameter("currentDate", currentDate);

                    final List<MySpotZone> mySpotZones = query.getResultList();

                    for(MySpotZone mySpotZone : mySpotZones) {
                        log.info("마이스팟 존 상태 변경 시작 : {}", mySpotZone.getId());
                        mySpotZone.updateMySpotZoneStatus(MySpotZoneStatus.OPEN);
                    }

                    log.info("마이스팟 존 상태 변경 완료 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob2_step2() {
        return stepBuilderFactory.get("pushAlarmJob2_step2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[user group 찾기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT ug FROM UserGroup ug LEFT JOIN MySpotZone msz ON ug.group = msz WHERE ug.clientStatus = 2 and msz.mySpotZoneStatus = 1";
                    final TypedQuery<UserGroup> query = entityManager.createQuery(queryString, UserGroup.class);

                    final List<UserGroup> userGroups = query.getResultList();

                    for(UserGroup userGroup : userGroups) {
                        log.info("user group 상태 변경 시작 : {}", userGroup.getId());
                        userGroup.updateStatus(ClientStatus.BELONG);
                    }

                    log.info("user group 상태 변경 완료 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob2_step3() {
        return stepBuilderFactory.get("pushAlarmJob2_step3")
                .<User, User>chunk(CHUNK_SIZE)
                .reader(openStatusMySpotZonePushAlarmReader())
                .processor(openStatusMySpotZonePushAlarmProcessor())
                .writer(openStatusMySpotZonePushAlarmWriter())
                .faultTolerant()
                .skip(ApiException.class) // Add the exception classes you want to skip
                .skip(RuntimeException.class)
                .build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob3_step1() {
        return stepBuilderFactory.get("pushAlarmJob3_step1")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[my spot zone 찾기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT msz FROM MySpotZone msz WHERE msz.closeDate = :currentDate and msz.mySpotZoneStatus = 1";
                    final LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
                    final TypedQuery<MySpotZone> query = entityManager.createQuery(queryString, MySpotZone.class);
                    query.setParameter("currentDate", currentDate);

                    final List<MySpotZone> mySpotZones = query.getResultList();

                    for(MySpotZone mySpotZone : mySpotZones) {
                        log.info("마이스팟 존 상태 변경 시작 : {}", mySpotZone.getId());
                        mySpotZone.updateMySpotZoneStatus(MySpotZoneStatus.CLOSE);
                    }

                    log.info("마이스팟 존 상태 변경 완료 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    @JobScope
    public Step pushAlarmJob3_step2() {
        return stepBuilderFactory.get("pushAlarmJob3_step2")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("[user group 찾기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

                    final String queryString = "SELECT ug FROM UserGroup ug LEFT JOIN MySpotZone msz ON ug.group = msz WHERE ug.clientStatus = 1 and msz.mySpotZoneStatus = 2";
                    final TypedQuery<UserGroup> query = entityManager.createQuery(queryString, UserGroup.class);

                    final List<UserGroup> userGroups = query.getResultList();

                    for(UserGroup userGroup : userGroups) {
                        log.info("user group 상태 변경 시작 : {}", userGroup.getId());
                        userGroup.updateStatus(ClientStatus.WAITING);
                    }

                    log.info("user group 상태 변경 완료 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
                    return RepeatStatus.FINISHED;
                })).build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> lastOrderTimePushAlarmReader() {
        log.info("[user 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        Set<BigInteger> groupIds = pushAlarmService.getGroupsForOneHourLeftLastOrderTime();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("groupIds", groupIds);


        if (groupIds.isEmpty()) {
            // Return an empty reader if orderItemIds is empty
            return null;
        }

        String queryString = "SELECT u FROM UserGroup ug LEFT JOIN User u ON u = ug.user WHERE ug.group.id in :groupIds";

        return new JpaPagingItemReaderBuilder<User>()
                .entityManagerFactory(entityManagerFactory) // Use the injected entityManagerFactory
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .parameterValues(parameterValues)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<User> openStatusMySpotZonePushAlarmReader() {
        log.info("[my spot zone user 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        List<BigInteger> userIds = pushAlarmService.getMySpotZoneOpenPushAlarmUserId();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("userIds", userIds);

        if (userIds.isEmpty()) {
            // Return an empty reader if orderItemIds is empty
            return new JpaPagingItemReaderBuilder<User>()
                    .name("EmptyReviewReader")
                    .build();
        }

        String queryString = "SELECT u FROM User u WHERE u.id in :userIds";


        return new JpaPagingItemReaderBuilder<User>()
                .entityManagerFactory(entityManagerFactory) // Use the injected entityManagerFactory
                .pageSize(100)
                .queryString(queryString)
                .name("JpaPagingItemReader")
                .parameterValues(Collections.singletonMap("userIds", userIds))
                .build();
    }

    @Bean
    @JobScope
    public ItemProcessor<User, User> lastOrderTimePushAlarmProcessor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                log.info("[User 푸시 알림 전송 시작] : {}", user.getId());
                try {
                    PushRequestDtoByUser pushRequestDto = pushUtil.getPushRequest(user, PushCondition.LAST_ORDER_BY_DAILYFOOD, null);
                    BatchAlarmDto batchAlarmDto = pushUtil.getBatchAlarmDto(pushRequestDto, user);
                    pushService.sendToPush(batchAlarmDto, PushCondition.LAST_ORDER_BY_DAILYFOOD);
                    applicationEventPublisher.publishEvent(new SseReceiverDto(user.getId(), 6, null, null, null));
                    pushUtil.savePushAlarmHash(batchAlarmDto.getTitle(), batchAlarmDto.getMessage(), user.getId(), AlarmType.MEAL, null);
                    log.info("[푸시알림 전송 성공] : {}", user.getId());
                } catch (Exception ignored) {
                    log.info("[푸시알림 전송 실패] : {}", user.getId());
                }
                return user;
            }
        };
    }

    @Bean
    @JobScope
    public ItemProcessor<User, User> openStatusMySpotZonePushAlarmProcessor() {
        return new ItemProcessor<User, User>() {
            @Override
            public User process(User user) throws Exception {
                log.info("[User 푸시 알림 전송 시작] : {}", user.getId());
                try {
                    PushCondition pushCondition = PushCondition.NEW_SPOT_2;
                    String customMessage = pushUtil.getContextOpenOrMySpot(user.getName(), GroupDataType.MY_SPOT.getType(), pushCondition);

                    PushRequestDtoByUser pushRequestDto = pushUtil.getPushRequest(user, pushCondition, customMessage);
                    BatchAlarmDto batchAlarmDto = pushUtil.getBatchAlarmDto(pushRequestDto, user);
                    pushService.sendToPush(batchAlarmDto, pushCondition);
                    applicationEventPublisher.publishEvent(new SseReceiverDto(user.getId(), 6, null, null, null));
                    pushUtil.savePushAlarmHash(batchAlarmDto.getTitle(), batchAlarmDto.getMessage(), user.getId(), AlarmType.SPOT_NOTICE, null);

                    log.info("[푸시알림 전송 성공] : {}", user.getId());
                } catch (Exception ignored) {
                    log.info("Exeption : " + ignored);
                    log.info("[푸시알림 전송 실패] : {}", user.getId());
                }
                return user;
            }
        };
    }

    @Bean
    @JobScope
    public JpaItemWriter<User> lastOrderTimePushAlarmWriter() {
        log.info("리뷰 푸시전송 완료 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<User>().entityManagerFactory(entityManagerFactory).build();
    }

    @Bean
    @JobScope
    public JpaItemWriter<User> openStatusMySpotZonePushAlarmWriter() {
        log.info("리뷰 푸시전송 완료 시작 : {}", DateUtils.localDateTimeToString(LocalDateTime.now()));
        return new JpaItemWriterBuilder<User>().entityManagerFactory(entityManagerFactory).build();
    }
}

