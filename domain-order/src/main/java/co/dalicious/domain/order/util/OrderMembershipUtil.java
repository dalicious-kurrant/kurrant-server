package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.mapper.OrderMembershipMapper;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.repository.MembershipSupportPriceRepository;
import co.dalicious.domain.order.repository.OrderItemMembershipRepository;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.system.util.PeriodDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrderMembershipUtil {
    private final OrderMembershipMapper orderMembershipMapper;
    private final OrderUserInfoMapper orderUserInfoMapper;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final MembershipSupportPriceRepository membershipSupportPriceRepository;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;

    // 기업 멤버십 가입 로직
    public void joinCorporationMembership(User user, Group group) {
        // 멤버십을 지원하는 기업의 식사를 주문하면서, 멤버십에 가입되지 않은 회원이라면 멤버십 가입.
        LocalDate now = LocalDate.now();
        LocalDate membershipStartDate = LocalDate.of(now.getYear(), now.getMonth(), group.getContractStartDate().getDayOfMonth());
        PeriodDto membershipPeriod = new PeriodDto(membershipStartDate, membershipStartDate.plusMonths(1));

        // 멤버십 등록
        Membership membership = orderMembershipMapper.toMembership(MembershipSubscriptionType.MONTH, user, membershipPeriod);
        membershipRepository.save(membership);

        // 결제 내역 등록
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipMapper.toOrderMembership(orderUserInfoDto, null, MembershipSubscriptionType.MONTH, BigDecimal.ZERO, BigDecimal.ZERO, PaymentType.SUPPORT_PRICE, membership);
        orderMembershipRepository.save(order);

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderMembershipMapper.toOrderItemMembership(order, membership);
        orderItemMembershipRepository.save(orderItemMembership);

        // 지원금 사용 등록
        MembershipSupportPrice membershipSupportPrice = orderMembershipMapper.toMembershipSupportPrice(user, group, orderItemMembership);
        membershipSupportPriceRepository.save(membershipSupportPrice);

        // 파운더스 확인
        if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
            Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
            foundersUtil.saveFounders(founders);
        }
    }
}
