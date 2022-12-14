package co.dalicious.data.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface CertificationHashRepository extends CrudRepository<CertificationHash, String> {
    CertificationHash findByCertificationNumber(String certificationNumber);
    CertificationHash findByTo(String to);
    void update(CertificationHash certificationHash);
    void delete(String id);
}
