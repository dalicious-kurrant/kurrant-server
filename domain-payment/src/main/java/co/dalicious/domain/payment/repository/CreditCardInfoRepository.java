package co.dalicious.domain.payment.repository;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface CreditCardInfoRepository  extends JpaRepository<CreditCardInfo, BigInteger> {
    Optional<CreditCardInfo> findOneByUserAndDefaultType(User user, Integer defaultType);

    CreditCardInfo findByUserId(BigInteger id);

    List<CreditCardInfo> findAllByUserId(BigInteger id);
}
