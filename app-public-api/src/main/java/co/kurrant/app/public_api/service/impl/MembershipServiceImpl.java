package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderMembership;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.repository.OrderMembershipRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.user.dto.PeriodDto;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.MembershipUtil;
import co.kurrant.app.public_api.dto.user.MembershipDto;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.MembershipService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final CommonService commonService;
    private final MembershipRepository membershipRepository;
    private final OrderMembershipRepository orderMembershipRepository;
    private final OrderRepository orderRepository;
    private final BigDecimal DELIVERY_FEE = new BigDecimal("2200.0");
    private final BigDecimal REFUND_YEARLY_MEMBERSHIP_PER_MONTH = BigDecimal.valueOf(MembershipSubscriptionType.YEAR.getDiscountedPrice() / 12);
    private final BigDecimal DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH = BigDecimal.valueOf(MembershipSubscriptionType.MONTH.getPrice()).subtract(REFUND_YEARLY_MEMBERSHIP_PER_MONTH);
    @Override
    public List<MembershipDto> retrieveMembership(HttpServletRequest httpServletRequest) {
        User user = commonService.getUser(httpServletRequest);
        // 멤버십 종료 날짜의 오름차순으로 멤버십 정보를 조회한다.

        // create a specification to specify the conditions of the query
        Specification<Membership> specification = (root, query, builder) ->
                builder.and(
                        builder.equal(root.get("user"), user),
                        builder.in(root.get("membershipStatus")).value(1).value(2)
                );

        // create a Sort object to specify the ordering of the query
        Sort sort = Sort.by(Sort.Direction.ASC, "endDate");

        // execute the query using the repository
        List<Membership> memberships = membershipRepository.findAll(specification, sort);

        List<MembershipDto> membershipDtos = new ArrayList<>();
        int membershipUsingPeriod = 0;
        for (Membership membership : memberships) {
            // 총 이용 기간을 계산한다.
            membershipUsingPeriod += MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), membership.getEndDate());
            // membershipDto를 생성한다.
            MembershipDto membershipDto = MembershipDto.builder()
                    .id(membership.getId())
                    .membershipSubscriptionType(membership.getMembershipSubscriptionType().getMembershipSubscriptionType())
                    .membershipUsingPeriod(membershipUsingPeriod)
                    .price(BigDecimal.valueOf(membership.getMembershipSubscriptionType().getPrice()))
                    .discountedPrice(BigDecimal.valueOf(membership.getMembershipSubscriptionType().getDiscountedPrice()))
                    .startDate(membership.getStartDate())
                    .endDate(membership.getEndDate())
                    .build();
            membershipDtos.add(membershipDto);
        }
        // 멤버십 종료 날짜의 내림차순으로 멤버십 이용내역을 반환한다.
        Collections.reverse(membershipDtos);
        return membershipDtos;
    }

    @Override
    public void saveMembershipAutoPayment(HttpServletRequest httpServletRequest) {

    }
}
