package co.kurrant.batch.job.batch.job;

import co.kurrant.batch.quartz.QuartzSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class RescheduleQuartzBatchJob implements Job {
    private final Scheduler scheduler;
    private final QuartzSchedule quartzSchedule;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            // Fetch the new cron expressions from the database
            List<String> newMakersAndFoodCron = quartzSchedule.getMakersAndFoodLastOrderTimeCron();
            List<String> newGroupCron = quartzSchedule.getGroupLastOrderTimeCron();
            List<String> newDeliveryTimeCron = quartzSchedule.getDeliveryTimeCron();

            // Reschedule the jobs
            rescheduleJob("dailyFoodJob2", newMakersAndFoodCron);
            rescheduleJob("dailyFoodJob1", newGroupCron);
            rescheduleJob("orderStatusToDeliveringJob", newDeliveryTimeCron);

        } catch (SchedulerException e) {
            throw new JobExecutionException(e);
        }
    }

    private void rescheduleJob(String jobName, List<String> newCrons) throws SchedulerException {
        // Create a list to hold the new cron triggers
        List<Trigger> newTriggers = new ArrayList<>();

        for (String newCron : newCrons) {
            // Generate the job key for each cron expression
            JobKey jobKey = JobKey.jobKey(createCronJobName(jobName, newCron));

            // Check if the job with the given key already exists
            if (scheduler.checkExists(jobKey)) {
                // If it exists, we'll just leave it be and add its trigger to the newTriggers list
                List<? extends Trigger> existingTriggers = scheduler.getTriggersOfJob(jobKey);
                newTriggers.addAll(existingTriggers);
            } else {
                // If it doesn't exist, create a new trigger and add it to the list
                Trigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(jobName + "_trigger_" + newCron.hashCode())
                        .withSchedule(CronScheduleBuilder.cronSchedule(newCron))
                        .forJob(jobKey)
                        .build();
                newTriggers.add(newTrigger);
            }
        }

        // Get all job keys in the group "DEFAULT" (or the group you are using)
        Set<JobKey> allJobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP));

        for (JobKey jobKey : allJobKeys) {
            // Check if the job key belongs to the jobName
            if (jobKey.getName().startsWith(jobName)) {
                // Get the triggers of the current job
                List<? extends Trigger> existingTriggers = scheduler.getTriggersOfJob(jobKey);

                for (Trigger existingTrigger : existingTriggers) {
                    if (!newTriggers.contains(existingTrigger)) {
                        // If the existing trigger does not exist in the newTriggers list, delete it
                        scheduler.unscheduleJob(existingTrigger.getKey());
                    }
                }
            }
        }
    }

    private String createCronJobName(String name, String cron) {
        return name + " (" + cron + ")";
    }
}
