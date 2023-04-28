package co.kurrant.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MembershipService {
    private final EntityManager entityManager;

    public List<BigInteger> getMembershipIds() {
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
}
