package co.kurrant.app.public_api.util;

import co.dalicious.data.redis.entity.CertificationHash;
import co.dalicious.data.redis.repository.CertificationHashRepository;
import co.dalicious.data.redis.RedisUtil;
import co.dalicious.system.util.RequiredAuth;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyUtil {
    private final CertificationHashRepository certificationHashRepository;
    private final RedisUtil redisUtil;

    // 인증 번호를 검증한다.
    public void verifyCertificationNumber(String key, RequiredAuth requiredAuth) {
        CertificationHash certificationHash  = certificationHashRepository.findByCertificationNumber(key);
        if (certificationHash == null) {
            throw new ApiException(ExceptionEnum.CERTIFICATION_NUMBER_NOT_FOUND);
        }
        String to = certificationHash.getTo();
        if(!requiredAuth.getId().equals(certificationHash.getType())) {
            throw new ApiException(ExceptionEnum.DOSE_NOT_CORRESPOND_CERTIFICATION_TYPE);
        }
        certificationHashRepository.delete(certificationHash);
        redisUtil.setDataExpire(to, requiredAuth.getId(), 500 * 1L);
    }

    // 인증된 사용자인지 확인한다.
    public void isAuthenticated(String to, RequiredAuth requiredAuth) {
        if(redisUtil.hasKey(to) && redisUtil.getData(to).equals(requiredAuth.getId())) {
            redisUtil.deleteData(to);
        } else {
            throw new ApiException(ExceptionEnum.UNAUTHORIZED);
        }
    }
}
