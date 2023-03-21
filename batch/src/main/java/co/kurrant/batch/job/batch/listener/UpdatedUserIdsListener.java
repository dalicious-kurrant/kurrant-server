package co.kurrant.batch.job.batch.listener;

import co.dalicious.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UpdatedUserIdsListener extends StepExecutionListenerSupport {
    private List<BigInteger> updatedUserIds = new ArrayList<>();

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("updatedUserIds", updatedUserIds);
        return stepExecution.getExitStatus();
    }

    public void addUser(User user) {
        updatedUserIds.add(user.getId());
    }
}

