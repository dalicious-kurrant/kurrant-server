package co.kurrant.batch.job.batch;

import co.dalicious.domain.user.entity.User;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class UpdatedUserIdsListener extends StepExecutionListenerSupport implements ItemWriteListener<User> {
    private List<BigInteger> updatedUserIds = new ArrayList<>();

    @Override
    public void beforeWrite(List<? extends User> users) {
        for (User user : users) {
            updatedUserIds.add(user.getId());
        }
    }

    @Override
    public void afterWrite(List<? extends User> users) {
    }

    @Override
    public void onWriteError(Exception exception, List<? extends User> users) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("updatedUserIds", updatedUserIds);
        return stepExecution.getExitStatus();
    }
}

