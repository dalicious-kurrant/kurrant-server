package co.kurrant.batch.job.batch.job;

import co.kurrant.batch.job.QuartzBatchJob;
import co.kurrant.batch.quartz.QuartzSchedule;
import co.kurrant.batch.quartz.QuartzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RescheduleQuartzBatchJob implements Job {
    @Autowired
    private QuartzService quartzService;
    @Autowired
    private QuartzSchedule quartzSchedule;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // Fetch the new cron expressions from the database
            List<String> newGroupCron = quartzSchedule.getGroupLastOrderTimeCron();
            List<String> newMakersAndFoodCron = quartzSchedule.getMakersAndFoodLastOrderTimeCron();
            List<String> newDeliveryTimeCron = quartzSchedule.getDeliveryTimeCron();

            // Reschedule the jobs
            quartzService.rescheduleJob(QuartzBatchJob.class, "dailyFoodJob1", "고객사 마감: DailyFood 상태 업데이트 Job", newGroupCron);
            log.info("dailyFoodJob1 cron 재설정");
            System.out.println("newGroupCron = " + newGroupCron);

            quartzService.rescheduleJob(QuartzBatchJob.class, "dailyFoodJob2", "메이커스 마감: DailyFood 상태 업데이트 Job", newMakersAndFoodCron);
            log.info("dailyFoodJob2 cron 재설정");
            System.out.println("newMakersAndFoodCron = " + newMakersAndFoodCron);

            quartzService.rescheduleJob(QuartzBatchJob.class, "orderStatusToDeliveringJob", "배송중으로 상태 업테이트 Job", newDeliveryTimeCron);
            log.info("orderStatusToDeliveringJob cron 재설정");
            System.out.println("newDeliveryTimeCron = " + newDeliveryTimeCron);

        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }


    private String createCronJobName(String name, String cron) {
        return name + " (" + cron + ")";
    }
}
