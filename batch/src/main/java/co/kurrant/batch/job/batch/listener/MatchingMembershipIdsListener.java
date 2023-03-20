package co.kurrant.batch.job.batch.listener;

import co.dalicious.system.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MatchingMembershipIdsListener implements StepExecutionListener {
    private final EntityManager entityManager;
    private List<BigInteger> membershipIds = new ArrayList<>();

    public MatchingMembershipIdsListener(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        membershipIds = matchingMembershipIds();
        stepExecution.getExecutionContext().put("membershipIds", membershipIds);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    private List<BigInteger> matchingMembershipIds() {
        log.info("[Membership 읽기 시작] : {} ", DateUtils.localDateTimeToString(LocalDateTime.now()));

        String queryString = "SELECT m.id FROM Membership m " +
                "JOIN m.user u " +
                "WHERE m.endDate <= NOW() " +
                "AND m.autoPayment = true " +
                "AND m.createdDateTime = (" +
                "   SELECT MAX(m2.createdDateTime) FROM Membership m2 WHERE m2.user = u" +
                ")";

        TypedQuery<BigInteger> query = entityManager.createQuery(queryString, BigInteger.class);

        return query.getResultList();
    }

    public List<BigInteger> getMembershipIds() {
        return membershipIds;
    }
}

