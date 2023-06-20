package co.dalicious.domain.order.util;

import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.MembershipSupportPrice;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.entity.OrderItemMembership;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.MonetaryStatus;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.mapper.OrderMembershipMapper;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.repository.MembershipSupportPriceRepository;
import co.dalicious.domain.order.repository.OrderItemMembershipRepository;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.QOrderItemDailyFoodRepository;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.repository.QMembershipRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.system.util.PeriodDto;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMembershipUtil {
    private final OrderMembershipMapper orderMembershipMapper;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final MembershipSupportPriceRepository membershipSupportPriceRepository;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;
    private final QOrderItemDailyFoodRepository qOrderItemDailyFoodRepository;

    // 기업 멤버십 가입 로직
    public void joinCorporationMembership(User user, Corporation corporation) {
        // 멤버십을 지원하는 기업의 식사를 주문하면서, 멤버십에 가입되지 않은 회원이라면 멤버십 가입.
        LocalDate now = LocalDate.now();
        LocalDate membershipStartDate = LocalDate.of(now.getYear(), now.getMonth(), corporation.getContractStartDate().getDayOfMonth());
        LocalDate membershipEndDate = (corporation.getMembershipEndDate() != null && corporation.getMembershipEndDate().isBefore(membershipStartDate.plusMonths(1)))
                ? corporation.getMembershipEndDate() : membershipStartDate.plusMonths(1);
        PeriodDto membershipPeriod = new PeriodDto(membershipStartDate, membershipEndDate);

        // 멤버십 등록
        Membership membership = orderMembershipMapper.toMembership(MembershipSubscriptionType.CORPORATION_MONTH, user, membershipPeriod);
        membershipRepository.save(membership);

        // 결제 내역 등록
        OrderMembership order = orderMembershipMapper.toOrderMembership(user, corporation.getAddress(), null, MembershipSubscriptionType.CORPORATION_MONTH, BigDecimal.ZERO, BigDecimal.ZERO, PaymentType.SUPPORT_PRICE, membership);
        orderMembershipRepository.save(order);

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderMembershipMapper.toOrderItemMembership(order, membership);
        orderItemMembershipRepository.save(orderItemMembership);

        // 지원금 사용 등록
        MembershipSupportPrice membershipSupportPrice = orderMembershipMapper.toMembershipSupportPrice(user, corporation, orderItemMembership);
        membershipSupportPriceRepository.save(membershipSupportPrice);

        // 파운더스 확인
        if (!foundersUtil.isFounders(user) && !foundersUtil.isOverFoundersLimit()) {
            Founders founders = foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber() + 1);
            foundersUtil.saveFounders(founders);
        }

        user.updateIsMembership(true);
    }

    // 기업 멤버십 환불 로직
    public void refundCorporationMembership(Membership membership) {
        membership.changeMembershipStatus(MembershipStatus.SUPPORT_PRICE_REFUND);
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(membership)
                .orElseThrow(() -> new ApiException(ExceptionEnum.ORDER_NOT_FOUND));
        orderItemMembership.updateOrderStatus(OrderStatus.CANCELED);
        orderItemMembership.getMembershipSupportPriceList()
                .forEach(membershipSupportPrice -> membershipSupportPrice.updateMonetaryStatus(MonetaryStatus.REFUND));
        membership.getUser().updateIsMembership(false);
    }

    // 기간 내에 첫번째로 구매한 음식인지
    public Boolean isFirstItemInMembershipPeriod(Membership membership, User user, OrderItemDailyFood orderItemDailyFood) {
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderItemDailyFoodRepository.findAllByUserAndPeriod(user, membership.getStartDate(), membership.getEndDate());
        return orderItemDailyFoods.size() == 1 && orderItemDailyFoods.get(0).equals(orderItemDailyFood);
    }
}
