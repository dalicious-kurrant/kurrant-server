package co.dalicious.data.redis.repository;

import co.dalicious.data.redis.entity.CertificationHash;
import org.springframework.data.repository.CrudRepository;

public interface CertificationHashRepository extends CrudRepository<CertificationHash, String> {
    CertificationHash findByCertificationNumber(String certificationNumber);
    CertificationHash findByTo(String to);
    void update(CertificationHash certificationHash);
    void delete(String id);
}
