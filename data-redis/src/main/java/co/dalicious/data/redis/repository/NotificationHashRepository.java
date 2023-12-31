package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.NotificationHash;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface NotificationHashRepository extends CrudRepository<NotificationHash, BigInteger> {
    List<NotificationHash> findAllByUserIdAndTypeAndIsRead(BigInteger userId, Integer type, boolean isRead);
    List<NotificationHash> findByUserIdAndTypeAndIsReadAndCreateDate(BigInteger userId, Integer type, boolean isRead, LocalDate date);
}
