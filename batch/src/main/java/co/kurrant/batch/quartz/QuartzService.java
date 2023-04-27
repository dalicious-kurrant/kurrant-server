package co.kurrant.batch.quartz;

import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.job.QuartzBatchJob;
import co.kurrant.batch.job.QuartzJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzService {
    private final Scheduler scheduler;
    public static final String JOB_NAME = "JOB_NAME";

    @PostConstruct
    public void initializeScheduler() {
        try {
            clearScheduler();
            registerJobListener();
            registerTriggerListener();

            Map<String, Object> jobParameters = createJobParameters();
            LocalDateTime currentTime = LocalDateTime.now();
            int executeCount = 1;
            String dateString = DateUtils.localDateTimeToString(currentTime);
            jobParameters.put("executeCount", executeCount);
            jobParameters.put("date", dateString);

//            addJob(QuartzBatchJob.class, "membershipPayJob1", "Membership 결제 Job", jobParameters, "0/10 * * * * ?");
            addJob(QuartzBatchJob.class, "dailyFoodJob2", "메이커스 마감: DailyFood 상태 업데이트 Job", jobParameters, "0 0/30 18-19,0-23,0-1 * * ?");
            addJob(QuartzBatchJob.class, "dailyFoodJob1", "고객사 마감: DailyFood 상태 업데이트 Job", jobParameters, "0 0/10 7-10,15-19,21-23,0-1 * * ?");
            addJob(QuartzBatchJob.class, "membershipPayJob1", "Membership 결제 Job", jobParameters, "0 0 4 * * ?");
            addJob(QuartzBatchJob.class, "userWithdrawalJob1", "User 탈퇴 Job", jobParameters, "0 0 4 * * ?");
            addJob(QuartzBatchJob.class, "orderStatusToDeliveringJob", "배송중으로 상태 업테이트 Job", jobParameters, "0 30/10 5-8,10-12,18-20 * * ?");
//            addJob(QuartzBatchJob.class, "orderStatusToDeliveredJob", "배송완료로 상태 업테이트 Job", jobParameters, "0 45/10 7-9,11-13,19-21 * * ?");
            addJob(QuartzBatchJob.class, "refreshTokenJob1", "Refresh Token 삭제 Job", jobParameters, "0 0 4 * * ?");
//            addJob(QuartzBatchJob.class, "refreshTokenJob1", "Refresh Token 삭제 Job", jobParameters, "0/10 * * * * ?");
        } catch (SchedulerException e) {
            log.error("addJob error : {}", e);
        }
    }

    // 1. 스케쥴러 초기화 -> DB CLEAR

    private void clearScheduler() throws SchedulerException {
        scheduler.clear();
    }
    // 2. Job Listener 등록

    private void registerJobListener() throws SchedulerException {
        scheduler.getListenerManager().addJobListener(new QuartzJobListener());
    }
    // 3. Trigger Listenser 등록

    private void registerTriggerListener() throws SchedulerException {
        scheduler.getListenerManager().addTriggerListener(new QuartzTriggerListener());
    }
    // 4. Job에 필요한 Parameter 생성

    private Map<String, Object> createJobParameters() {
        return new HashMap<>();
    }
    // 5. Job 생성 및 Scheduler에 등록
    public <T extends Job> void addJob(Class<? extends Job> job, String name, String description, Map<String, Object> parameters, String cron) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job, name, description, parameters);
        Trigger trigger = buildCronTrigger(cron);
        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private JobDetail buildJobDetail(Class<? extends Job> job, String name, String description, Map<String, Object> parameters) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_NAME, name);
        jobDataMap.putAll(parameters);

        return JobBuilder.newJob(job)
                .withIdentity(name)
                .withDescription(description)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildCronTrigger(String cron) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
    }
}
