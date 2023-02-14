package co.dalicious.domain.order.repository;

import co.dalicious.domain.order.entity.MembershipSupportPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MembershipSupportPriceRepository extends JpaRepository<MembershipSupportPrice, BigInteger> {
}