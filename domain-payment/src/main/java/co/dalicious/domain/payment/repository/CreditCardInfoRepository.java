package co.dalicious.domain.payment.repository;

import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.user.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface CreditCardInfoRepository  extends JpaRepository<CreditCardInfo, BigInteger> {

}
