package co.kurrant.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class 소QuartzJob implements Job {
    /*
     * Quartz instantiates jobs using a no-argument constructor,
     * and it does not have access to the Spring container to perform dependency injection.
     * so, cannot use constructor dependency injection with Quartz jobs.
     * */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Quartz Job 실행");

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        log.info("dataMap date : {}", dataMap.get("date"));
        log.info("dataMap executeCount : {}", dataMap.get("executeCount"));

        // JobDataMap을 통해 Job의 실행 횟수를 받아서 +1을 한다.
        int count = (int) dataMap.get("executeCount");
        dataMap.put("executeCount", ++count);
    }
}
