package co.kurrant.batch.quartz;

import co.dalicious.system.util.DateUtils;
import co.kurrant.batch.job.QuartzBatchJob;
import co.kurrant.batch.job.batch.job.RescheduleQuartzBatchJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzService {
    private final Scheduler scheduler;
    private final QuartzSchedule quartzSchedule;
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
//            addJob(QuartzBatchJob.class, "makersPaycheckJob", "메이커스 정산 배치 시작", jobParameters, "0/10 * * * * ?");
//            addJob(QuartzBatchJob.class, "orderStatusToDeliveredJob", "배송완료로 상태 업테이트 Job", jobParameters, "0 45/10 7-9,11-13,19-21 * * ?");
//            addJob(QuartzBatchJob.class, "dailyFoodJob1", "고객사 마감: DailyFood 상태 업데이트 Job", jobParameters, "0/10 * * * * ?");
//            addJob(QuartzBatchJob.class, "userWithdrawalJob1", "User 탈퇴 Job", jobParameters, "0 * * * * ?");
            
//            addJob(QuartzBatchJob.class, "dailyFoodJob2", "메이커스 마감: DailyFood 상태 업데이트 Job", jobParameters, "0 0/30 0,18 * * ?");
//            addJob(QuartzBatchJob.class, "dailyFoodJob1", "고객사 마감: DailyFood 상태 업데이트 Job", jobParameters, "0 0/10 7-10,15-19,21-23,0-1 * * ?");
//            addJob(QuartzBatchJob.class, "userWithdrawalJob1", "User 탈퇴 Job", jobParameters, "0 0 3 * * ?");
//            addJob(QuartzBatchJob.class, "orderStatusToDeliveringJob", "배송중으로 상태 업테이트 Job", jobParameters, "0 30/10 5-8,10-12,18-20 * * ?");
//            addJob(QuartzBatchJob.class, "refreshTokenJob1", "Refresh Token 삭제 Job", jobParameters, "0 0 4 * * ?");
//            addJob(QuartzBatchJob.class, "membershipPayJob1", "Membership 결제 Job", jobParameters, "0 0/5 13 * * ?");
//            addJob(QuartzBatchJob.class, "reviewJob1", "review 마감시간 푸시알림 Job", jobParameters, "0 0/10 11 * * ?");
            addJob(QuartzBatchJob.class, "pushAlarmJob2", "my spot zone 오픈 푸시알림 Job", jobParameters, "0 0/30 16 * * ?");
            addJob(QuartzBatchJob.class, "dailyFoodJob2", "메이커스 마감: DailyFood 상태 업데이트 Job", jobParameters, quartzSchedule.getMakersAndFoodLastOrderTimeCron());
            addJob(QuartzBatchJob.class, "dailyFoodJob1", "고객사 마감: DailyFood 상태 업데이트 Job", jobParameters, quartzSchedule.getGroupLastOrderTimeCron());
            addJob(QuartzBatchJob.class, "orderStatusToDeliveringJob", "배송중으로 상태 업테이트 Job", jobParameters, quartzSchedule.getDeliveryTimeCron());
            addJob(QuartzBatchJob.class, "reviewJob1", "review 마감시간 푸시알림 Job", jobParameters, "0 0/10 11 * * ?");
            addJob(QuartzBatchJob.class, "userWithdrawalJob1", "User 탈퇴 Job", jobParameters, "0 0 3 * * ?");
            addJob(QuartzBatchJob.class, "refreshTokenJob1", "Refresh Token 삭제 Job", jobParameters, "0 0 4 * * ?");
            addJob(QuartzBatchJob.class, "membershipPayJob1", "Membership 결제 Job", jobParameters, "0 0/5 13 * * ?");

            addJob(RescheduleQuartzBatchJob.class, "rescheduleJob", "Reschedule Job", jobParameters, "0 0 0 * * ?");
//            addJob(QuartzBatchJob.class, "pushAlarmJob1", "음식 마감시간 푸시알림 Job", jobParameters, "0 5/7 7-10,15-19,21-23,0-1 * * ?");

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

    public <T extends Job> void addJob(Class<? extends Job> job, String name, String description, Map<String, Object> parameters, List<String> crons) throws SchedulerException {
        for (String cron : crons) {
            // Don't append the cron expression to the job name
            JobDetail jobDetail = buildJobDetail(job, name, description, parameters);

            // Check if the job with the given key already exists
            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }

            // When building the trigger, use a different identity for each cron trigger
            Trigger trigger = buildCronTrigger(name + createCronJobName(name, cron), cron, jobDetail);
            scheduler.scheduleJob(jobDetail, trigger);
        }
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

    public Trigger buildCronTrigger(String triggerIdentity, String cronExpression, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerIdentity)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .forJob(jobDetail)
                .build();
    }

    public void rescheduleJob(String jobName, List<String> newCrons) throws SchedulerException  {
        // Generate the job key
        JobKey jobKey = JobKey.jobKey(jobName);

        if (!scheduler.checkExists(jobKey)) {
            throw new SchedulerException("Job with key " + jobKey + " not found");
        }

        // Get the triggers of the current job
        List<? extends Trigger> existingTriggers = scheduler.getTriggersOfJob(jobKey);

        // Unscheduling existing triggers which are not part of newCrons
        for (Trigger existingTrigger : existingTriggers) {
            if (!newCrons.contains(((CronTrigger) existingTrigger).getCronExpression())) {
                scheduler.unscheduleJob(existingTrigger.getKey());
            }
        }

        // Adding new triggers which are not part of existing triggers
        for (String newCron : newCrons) {
            boolean isTriggerExist = false;

            for (Trigger existingTrigger : existingTriggers) {
                if (newCron.equals(((CronTrigger) existingTrigger).getCronExpression())) {
                    isTriggerExist = true;
                    break;
                }
            }

            if (!isTriggerExist) {
                Trigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobName + "_trigger_" + newCron.hashCode())
                        .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                        .forJob(jobKey)
                        .build();
                scheduler.scheduleJob(newTrigger);
            }
        }
    }

    private String createCronJobName(String name, String cron) {
        return name + " (" + cron + ")";
    }
}
