package co.kurrant.batch.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

@Slf4j
public class QuartzTriggerListener implements TriggerListener {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        log.info("Trigger 실행");
    }

    @Override
    // 결과가 True이면 JobListener jobExecutionVetoed(Job 중단) 실행
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        log.info("Trigger 상태 체크");
        JobDataMap map = context.getJobDetail().getJobDataMap();

        int executeCount = 1;
        if(map.containsKey("executeCount")) {
            executeCount = (int) map.get("executeCount");
        }
        return executeCount >= 2;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {

    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info("Trigger 성공");
    }
}
