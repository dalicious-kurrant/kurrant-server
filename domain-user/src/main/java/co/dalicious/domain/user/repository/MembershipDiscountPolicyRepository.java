package co.dalicious.domain.user.repository;

import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface MembershipDiscountPolicyRepository extends JpaRepository<MembershipDiscountPolicy, BigInteger> {
}
