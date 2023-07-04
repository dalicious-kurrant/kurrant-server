package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.PushAlarmHash;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface PushAlarmHashRepository extends CrudRepository<PushAlarmHash, String> {
    List<PushAlarmHash> findAllByUserIdOrderByCreatedDateTimeDesc(BigInteger userId);
    void deleteAllByUserId(BigInteger userId);
    void update(PushAlarmHash certificationHash);
    void delete(String id);
    List<PushAlarmHash> findAllPushAlarmHashByUserIdAndIsRead(BigInteger userId, Boolean isRead);
    List<PushAlarmHash> findAllPushAlarmHashByUserIdAndIds(BigInteger userId, List<String> ids);
}
