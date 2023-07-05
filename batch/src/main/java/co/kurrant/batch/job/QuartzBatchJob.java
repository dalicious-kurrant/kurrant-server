package co.kurrant.batch.job;

import co.kurrant.batch.batch.BeanUtil;
import co.kurrant.batch.quartz.QuartzService;
import lombok.SneakyThrows;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class QuartzBatchJob extends QuartzJob{
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BeanUtil beanUtil;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        // 전달받은 JobDataMap에서 Job 이름을 꺼내오고 그 Job이름으로 context에서 Bean을 가져온다.
        // Extract the job name with cron from the JobDataMap
        String jobNameWithCron = (String) jobDataMap.get(QuartzService.JOB_NAME);

        // Remove the cron expression from the job name
        String jobName = extractJobName(jobNameWithCron);

        Job job = (Job) beanUtil.getBean(jobName);

        JobParameters jobParameter = new JobParametersBuilder()
                .addDate("currentDate", new Date())
                .toJobParameters();

        jobLauncher.run(job, jobParameter);
    }

    private String extractJobName(String jobNameWithCron) {
        return jobNameWithCron.replaceAll(" \\(.*\\)$", "");
    }
}
