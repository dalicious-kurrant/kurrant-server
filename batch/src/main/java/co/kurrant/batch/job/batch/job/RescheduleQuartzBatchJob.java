package co.kurrant.batch.job.batch.job;

import co.kurrant.batch.quartz.QuartzSchedule;
import co.kurrant.batch.quartz.QuartzService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class RescheduleQuartzBatchJob implements Job {
    private final QuartzService quartzService;
    private final QuartzSchedule quartzSchedule;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // Fetch the new cron expressions from the database
            List<String> newMakersAndFoodCron = quartzSchedule.getMakersAndFoodLastOrderTimeCron();
            List<String> newGroupCron = quartzSchedule.getGroupLastOrderTimeCron();
            List<String> newDeliveryTimeCron = quartzSchedule.getDeliveryTimeCron();

            // Reschedule the jobs
            quartzService.rescheduleJob("dailyFoodJob2", newMakersAndFoodCron);
            log.info("dailyFoodJob2 cron 재설정");
            quartzService.rescheduleJob("dailyFoodJob1", newGroupCron);
            log.info("dailyFoodJob1 cron 재설정");
            quartzService.rescheduleJob("orderStatusToDeliveringJob", newDeliveryTimeCron);
            log.info("orderStatusToDeliveringJob cron 재설정");

        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }



    private String createCronJobName(String name, String cron) {
        return name + " (" + cron + ")";
    }
}
