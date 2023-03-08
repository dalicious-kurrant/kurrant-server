package co.kurrant.batch.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
// Job 실행 전후 event를 걸어주는 역할
public class QuartzJobListener implements JobListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("Job 수행되기 전");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("Job 중단");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("Job 수행 완료 후");
    }
}
