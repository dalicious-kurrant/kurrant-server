package co.dalicious.domain.event;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.system.enums.DiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipDiscountEventImpl implements MembershipDiscountEvent {
    private final MembershipRepository membershipRepository;

    public MembershipDiscountPolicy bespinGlobalEvent(User user, Membership membership) {
        // 베스핀글로벌 유저가 아니라면 기간할인 무시
        if (!user.getEmail().contains("@bespinglobal.com")) {
            return null;
        }
        // 기존에 멤버십을 구매한 이력이 있다면 기간할인 무시
        List<Membership> memberships = membershipRepository.findAllByUser(user);
        if (!memberships.isEmpty()) {
            return null;
        }
        Integer discountRate = 0;
        if (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.MONTH)) {
            discountRate = 50;
        } else if (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.YEAR)) {
            discountRate = 30;
        }
        return MembershipDiscountPolicy.builder()
                .discountType(DiscountType.PERIOD_DISCOUNT)
                .discountRate(discountRate)
                .membership(membership)
                .build();
    }

    public Boolean isBespinGlobal(User user) {
        List<Membership> memberships = membershipRepository.findAllByUser(user);
        if (!memberships.isEmpty()) return false;
        return user.getEmail().contains("@bespinglobal.com");
    }
}
