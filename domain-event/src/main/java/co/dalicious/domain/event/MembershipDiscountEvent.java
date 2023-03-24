package co.dalicious.domain.event;

import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;

import java.util.List;

public interface MembershipDiscountEvent {
    MembershipDiscountPolicy bespinGlobalEvent(User user, Membership membership);
    Boolean isBespinGlobal(User user);
}
