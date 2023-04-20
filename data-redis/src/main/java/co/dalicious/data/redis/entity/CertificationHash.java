package co.dalicious.data.redis.entity;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;


@Getter
@RedisHash(value = "certificationHash", timeToLive = 180)
public class CertificationHash {
    @Id
    String id;

    @Indexed
    String to;

    String type;
    @Indexed
    String certificationNumber;
    Boolean isAuthenticated;

    @Builder
    public CertificationHash(String id, String to, String type, String certificationNumber, Boolean isAuthenticated) {
        this.id = id;
        this.to = to;
        this.type = type;
        this.certificationNumber = certificationNumber;
        this.isAuthenticated = isAuthenticated;
    }
}
