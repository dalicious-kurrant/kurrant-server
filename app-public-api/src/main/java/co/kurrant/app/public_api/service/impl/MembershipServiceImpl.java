package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.dalicious.domain.order.dto.OrderMembershipResDto;
import co.dalicious.domain.order.dto.OrderUserInfoDto;
import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.entity.enums.OrderType;
import co.dalicious.domain.order.mapper.OrderMembershipMapper;
import co.dalicious.domain.order.mapper.OrderMembershipResMapper;
import co.dalicious.domain.order.mapper.OrderUserInfoMapper;
import co.dalicious.domain.order.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.order.repository.*;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.order.service.DiscountPolicy;
import co.dalicious.domain.order.util.OrderUtil;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.enums.PaymentCompany;
import co.dalicious.domain.payment.repository.CreditCardInfoRepository;
import co.dalicious.domain.payment.util.CreditCardValidator;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.dto.DailyFoodMembershipDiscountDto;
import co.dalicious.domain.user.dto.MembershipBenefitDto;
import co.dalicious.domain.user.dto.MembershipDto;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.MembershipDiscountPolicy;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.MembershipStatus;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.user.entity.enums.PaymentType;
import co.dalicious.domain.user.mapper.FoundersMapper;
import co.dalicious.domain.user.mapper.MembershipBenefitMapper;
import co.dalicious.domain.user.repository.FoundersRepository;
import co.dalicious.domain.user.repository.MembershipDiscountPolicyRepository;
import co.dalicious.domain.user.repository.MembershipRepository;
import co.dalicious.domain.user.util.FoundersUtil;
import co.dalicious.domain.user.util.MembershipUtil;
import co.dalicious.system.util.PeriodDto;
import co.dalicious.system.util.enums.DiscountType;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.repository.QMembershipRepository;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.MembershipService;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final MembershipRepository membershipRepository;
    private final QMembershipRepository QmembershipRepository;
    private final OrderItemMembershipRepository orderItemMembershipRepository;
    private final CreditCardInfoRepository creditCardInfoRepository;
    private final FoundersUtil foundersUtil;
    private final FoundersMapper foundersMapper;
    private final OrderMembershipRepository orderMembershipRepository;
    private final BigDecimal REFUND_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.YEAR.getPrice().multiply(BigDecimal.valueOf((100 - MembershipSubscriptionType.YEAR.getDiscountRate()) * 0.01)).divide(BigDecimal.valueOf(12));
    private final BigDecimal DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH = MembershipSubscriptionType.MONTH.getPrice().subtract(REFUND_YEARLY_MEMBERSHIP_PER_MONTH);
    private final MembershipDiscountPolicyRepository membershipDiscountPolicyRepository;
    private final DiscountPolicy discountPolicy;
    private final OrderUserInfoMapper orderUserInfoMapper;
    private final OrderMembershipResMapper orderMembershipResMapper;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    private final DeliveryFeePolicy deliveryFeePolicy;
    private final MembershipBenefitMapper membershipBenefitMapper;
    private final QMembershipRepository qMembershipRepository;
    private final OrderUtil orderUtil;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;
    private final OrderMembershipMapper orderMembershipMapper;

    @Override
    @Transactional
    public void joinMembership(SecurityUser securityUser, OrderMembershipReqDto orderMembershipReqDto) {
        User user = userUtil.getUser(securityUser);
        // 금액 정보가 일치하는지 확인
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(orderMembershipReqDto.getSubscriptionType());
        // 1. 기본 가격이 일치하는지 확인
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        if (!(orderMembershipReqDto.getDefaultPrice().compareTo(defaultPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 2. 연간 구독 할인 가격이 일치하는지 확인
        BigDecimal yearDescriptionDiscountPrice = OrderUtil.discountPriceByRate(membershipSubscriptionType.getPrice(), membershipSubscriptionType.getDiscountRate());
        if (!(orderMembershipReqDto.getYearDescriptionDiscountPrice().compareTo(yearDescriptionDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 3. 기간 할인 가격이 일치하는지 확인
        // TODO: 기간할인 추가시 기간할인 조회 로직 추가 필요
        BigDecimal periodDiscountPrice = BigDecimal.ZERO;
        if (!(orderMembershipReqDto.getPeriodDiscountPrice().compareTo(periodDiscountPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }
        // 4. 총 가격이 일치하는지 확인
        BigDecimal totalPrice = defaultPrice.subtract(yearDescriptionDiscountPrice).subtract(periodDiscountPrice);
        if(!(orderMembershipReqDto.getTotalPrice().compareTo(totalPrice) == 0)) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 5. 이 사람이 기존에 멤버십을 가입했는 지 확인 후 멤버십 기간 저장
        PeriodDto periodDto = null;
        if (user.getIsMembership()) {
            Membership membership = qMembershipRepository.findUserCurrentMembership(user, LocalDate.now());
            if (membership != null) {
                LocalDate currantEndDate = membership.getEndDate();
                if(LocalDate.now().isBefore(currantEndDate)) {
                    throw new ApiException(ExceptionEnum.ALREADY_EXISTING_MEMBERSHIP);
                }
                // 구독 타입에 따라 기간 정하기
                periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                        MembershipUtil.getStartAndEndDateMonthly(currantEndDate) :
                        MembershipUtil.getStartAndEndDateYearly(currantEndDate);
            }
        } else {
            LocalDate now = LocalDate.now();
            // 구독 타입에 따라 기간 정하기
            periodDto = (membershipSubscriptionType.equals(MembershipSubscriptionType.MONTH)) ?
                    MembershipUtil.getStartAndEndDateMonthly(now) :
                    MembershipUtil.getStartAndEndDateYearly(now);
        }

        assert periodDto != null;
        // 멤버십 등록
        Membership membership = membershipRepository.save(orderMembershipMapper.toMembership(membershipSubscriptionType, user, periodDto));

        // 연간 구독 구매자라면, 할인 정책 저장.
        if(membershipSubscriptionType.equals(MembershipSubscriptionType.YEAR)) {
            MembershipDiscountPolicy yearDescriptionDiscountPolicy = orderMembershipMapper.toMembershipDiscountPolicy(membership, DiscountType.YEAR_DESCRIPTION_DISCOUNT);
            membershipDiscountPolicyRepository.save(yearDescriptionDiscountPolicy);
        }


        /* TODO: 할인 혜택을 가지고 있는 유저인지 확인 후 할인 정책 저장.
        MembershipDiscountPolicy periodDiscountPolicy = MembershipDiscountPolicy.builder()
                .membership(membership)
                .discountRate(membershipSubscriptionType.getDiscountRate())
                .discountType(DiscountType.PERIOD_DISCOUNT)
                .build();
        membershipDiscountPolicyRepository.save(periodDiscountPolicy);
         */

        //카드정보 가져오기
        CreditCardInfo creditCardInfo = creditCardInfoRepository.findById(orderMembershipReqDto.getCardId()).
                orElseThrow(() -> new ApiException(ExceptionEnum.CARD_NOT_FOUND));
        CreditCardValidator.isValidCreditCard(creditCardInfo, user);

        // 멤버십 결제 요청
        OrderUserInfoDto orderUserInfoDto = orderUserInfoMapper.toDto(user);
        OrderMembership order = orderMembershipRepository.save(orderMembershipMapper.toOrderMembership(orderUserInfoDto, creditCardInfo, membershipSubscriptionType, BigDecimal.ZERO, totalPrice, PaymentType.ofCode(orderMembershipReqDto.getPaymentType()), membership));

        // 멤버십 결제 내역 등록(진행중 상태)
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.save(orderMembershipMapper.toOrderItemMembership(order, membership));

        // 파운더스 확인
        if(!foundersUtil.isFounders(user) &&!foundersUtil.isOverFoundersLimit()) {
            Founders founders =  foundersMapper.toEntity(user, membership, foundersUtil.getMaxFoundersNumber()+1);
            foundersUtil.saveFounders(founders);
        }

        // 결제 진행. 실패시 오류 날림
        BigDecimal price = discountPolicy.orderItemTotalPrice(orderItemMembership);

        String customerKey = creditCardInfo.getCustomerKey();
        String billingKey = creditCardInfo.getBillingKey();

        try {
            JSONObject payResult = tossUtil.payToCard(customerKey, price.intValue(), orderItemMembership.getOrder().getCode(), orderItemMembership.getMembershipSubscriptionType(), billingKey);

            // 결제 성공시 orderMembership의 상태값을 결제 성공 상태(1)로 변경
            if (payResult.get("status").equals("DONE")) {
                order.updateDefaultPrice(defaultPrice);
                order.updateTotalPrice(price);
                orderItemMembership.updateDiscountPrice(membership.getMembershipSubscriptionType().getPrice().subtract(price));
                orderItemMembership.updateOrderStatus(OrderStatus.COMPLETED);

                //Order 테이블에 paymentKey와 receiptUrl 업데이트
                JSONObject receipt = (JSONObject) payResult.get("receipt");
                String receiptUrl = receipt.get("url").toString();

                String paymentKey = (String) payResult.get("paymentKey");

                JSONObject card = (JSONObject) payResult.get("card");
                String paymentCompanyCode;
                if(card == null) {
                    JSONObject easyPay = (JSONObject) payResult.get("easyPay");
                    if(easyPay == null) {
                        throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
                    }
                    paymentCompanyCode = (String) easyPay.get("provider");
                } else {
                    paymentCompanyCode = (String) card.get("issuerCode");
                }
                PaymentCompany paymentCompany = PaymentCompany.ofCode(paymentCompanyCode);
                qOrderDailyFoodRepository.afterPaymentUpdate(receiptUrl, paymentKey, orderItemMembership.getOrder().getId(), paymentCompany);
            }
            // 결제 실패시 orderMembership의 상태값을 결제 실패 상태(3)로 변경
            else {
                orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
                throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
            }
        } catch (ApiException e) {
            orderItemMembership.updateOrderStatus(OrderStatus.FAILED);
            throw new ApiException(ExceptionEnum.PAYMENT_FAILED);
        } catch (IOException | InterruptedException | ParseException e) {
            throw new RuntimeException(e);
        }
        user.changeMembershipStatus(true);
    }

    @Override
    public OrderMembershipResDto getOrderMembership(SecurityUser securityUser, Integer subscriptionType) {
        // TODO: 결제 수단 구현 완료시 Response 값에 유저 결제 수단 정보가 포함될 수 있도록 한다.
        MembershipSubscriptionType membershipSubscriptionType = MembershipSubscriptionType.ofCode(subscriptionType);
        BigDecimal defaultPrice = membershipSubscriptionType.getPrice();
        // 1. 월간 구독인지 연간 구독인지 확인 후 할인 금액 도출
        Integer yearSubscriptionDiscountRate = 0;
        if (membershipSubscriptionType == MembershipSubscriptionType.YEAR) {
            yearSubscriptionDiscountRate = membershipSubscriptionType.getDiscountRate();
        }
        BigDecimal yearSubscriptionDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice, yearSubscriptionDiscountRate);
        // 2. 기간 할인이 적용되는 유저인지 확인
        User user = userUtil.getUser(securityUser);
        Integer periodDiscountRate = 0;
        BigDecimal periodDiscountPrice = OrderUtil.discountPriceByRate(defaultPrice.subtract(yearSubscriptionDiscountPrice), periodDiscountRate);
        // 3. 할인이 적용된 최종 가격 도출
        return OrderMembershipResDto.builder()
                .subscriptionType(subscriptionType)
                .defaultPrice(defaultPrice)
                .yearDescriptionDiscountPrice(yearSubscriptionDiscountPrice)
                .periodDiscountPrice(periodDiscountPrice)
                .totalPrice(defaultPrice.subtract(yearSubscriptionDiscountPrice).subtract(periodDiscountPrice))
                .build();
    }

    @Override
    @Transactional
    public void refundMembership(User user, Order order, Membership membership, OrderMembership orderMembership) throws IOException, ParseException {
        // TODO: 연간구독 해지시, membership endDate update.
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findByUserAndServiceDateBetween(user, membership.getStartDate(), membership.getEndDate());

        // 멤버십 결제금액 가져오기
        BigDecimal paidPrice = order.getTotalPrice();

        // 멤버십 결제 가져오기
        List<OrderItem> orderItems = order.getOrderItems();
        OrderItemMembership orderItemMembership = null;
        for (OrderItem orderItem : orderItems) {
            if(orderItem.getOrderStatus().equals(OrderStatus.COMPLETED)) {
                orderItemMembership = (OrderItemMembership) orderItem;
            }
        }
        if(orderItemMembership == null) {
            throw new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND);
        }

        // 환불 가능 금액 계산하기
        BigDecimal refundPrice = getRefundableMembershipPrice(orderItemDailyFoods, orderItemMembership);

        // 자동 환불 설정
        orderItemMembership.updateOrderStatus(OrderStatus.AUTO_REFUND);
        OrderUtil.orderMembershipStatusUpdate(user, orderItemMembership);

        // 주문 상태 변경
        if(paidPrice.compareTo(refundPrice) < 0) {
            throw new ApiException(ExceptionEnum.PRICE_INTEGRITY_ERROR);
        }

        // 취소 내역 저장
        PaymentCancelHistory paymentCancelHistory = orderUtil.cancelOrderItemMembership(order.getPaymentKey(), orderMembership.getCreditCardInfo(), "멤버십 환불", orderItemMembership, refundPrice);
        paymentCancelHistoryRepository.save(paymentCancelHistory);
    }

    @Override
    @Transactional
    public void unsubscribeMembership(SecurityUser securityUser) throws IOException, ParseException {
        // 유저 가져오기
        User user = userUtil.getUser(securityUser);
        // 멤버십 사용중인 유저인지 가져오기
        if (!user.getIsMembership()) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 현재 사용중인 멤버십 가져오기
        Membership userCurrentMembership = QmembershipRepository.findUserCurrentMembership(user, LocalDate.now());

        if (userCurrentMembership == null) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }
        // 멤버십 주문 가져오기
        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );
        OrderMembership orderMembership = orderMembershipRepository.findOneByMembership(userCurrentMembership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );

        // 사용한 날짜 계산하기
        int membershipUsingDays = userCurrentMembership.getStartDate().until(LocalDate.now()).getDays();

        // 7일 이하일 경우 멤버십 환불
        if (membershipUsingDays <= 7) {
            refundMembership(user, orderItemMembership.getOrder(), userCurrentMembership, orderMembership);
        }
        userCurrentMembership.changeAutoPaymentStatus(false);
    }

    @Override
    @Transactional
    public MembershipBenefitDto getMembershipBenefit(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthAgo = LocalDate.now().minusMonths(3).atStartOfDay();

        Membership membership = qMembershipRepository.findUserCurrentMembership(user, now.toLocalDate());

        OrderItemMembership orderItemMembership = orderItemMembershipRepository.findOneByMembership(membership).orElseThrow(
                () -> new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND)
        );

        if(membership == null) {
            throw new ApiException(ExceptionEnum.MEMBERSHIP_NOT_FOUND);
        }

        // 멤버십 이용 금액 혜택을 받은 주문 상품 가져오기
        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllWhichGetMembershipBenefit(user, now, threeMonthAgo);

        // 최근 3개월동안 멤버십을 통해 할인 받은 정기식사 할인 금액을 가져온다.
        DailyFoodMembershipDiscountDto dailyFoodMembershipDiscountDto = getDailyFoodPriceBenefits(orderItemDailyFoods);

        // 환불 가능 금액 계산하기
        BigDecimal refundablePrice = getRefundableMembershipPrice(orderItemDailyFoods, orderItemMembership);

        return membershipBenefitMapper.toDto(membership, dailyFoodMembershipDiscountDto, refundablePrice);
    }

    @Override
    @Transactional
    public DailyFoodMembershipDiscountDto getDailyFoodPriceBenefits(List<OrderItemDailyFood> orderItemDailyFoods) {
        BigDecimal totalMembershipDiscountPrice = BigDecimal.ZERO;
        BigDecimal totalMembershipDiscountDeliveryFee = BigDecimal.ZERO;
        Set<OrderItemDailyFoodGroup> orderItemDailyFoodGroups = new HashSet<>();
        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoods) {
            totalMembershipDiscountPrice = totalMembershipDiscountPrice.add(orderItemDailyFood.getMembershipDiscountPrice());
            orderItemDailyFoodGroups.add(orderItemDailyFood.getOrderItemDailyFoodGroup());
        }
        totalMembershipDiscountDeliveryFee = totalMembershipDiscountDeliveryFee.add(deliveryFeePolicy.getDeliveryFee().multiply(BigDecimal.valueOf(orderItemDailyFoodGroups.size())));
        return new DailyFoodMembershipDiscountDto(totalMembershipDiscountPrice, totalMembershipDiscountDeliveryFee);
    }

    @Override
    public BigDecimal getRefundableMembershipPrice(List<OrderItemDailyFood> orderItemDailyFoods, OrderItemMembership orderItemMembership) {
        Membership membership = orderItemMembership.getMembership();
        List<OrderItemDailyFood> orderItemDailyFoodList = orderItemDailyFoods.stream().filter(v -> v.getCreatedDateTime().after(Timestamp.valueOf(membership.getStartDate().atStartOfDay())) &&
                v.getCreatedDateTime().before(Timestamp.valueOf(membership.getEndDate().atTime(23, 59, 59)))).toList();
        DailyFoodMembershipDiscountDto dailyFoodCurrentMembershipDiscountDto = getDailyFoodPriceBenefits(orderItemDailyFoodList);

        return orderItemMembership.getOrder().getTotalPrice().subtract(dailyFoodCurrentMembershipDiscountDto.getTotalMembershipDiscountPrice()).subtract(dailyFoodCurrentMembershipDiscountDto.getTotalMembershipDiscountDeliveryFee());

    }

    @Override
    @Transactional
    public List<MembershipDto> retrieveMembership(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
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
        List<OrderItemMembership> orderItemMemberships = orderItemMembershipRepository.findAllByMembership(memberships);

        List<MembershipDto> membershipDtos = new ArrayList<>();
        int membershipUsingPeriod = 0;
        for (OrderItemMembership orderItemMembership : orderItemMemberships) {
            membershipUsingPeriod += MembershipUtil.getPeriodWithStartAndEndDate(orderItemMembership.getMembership().getStartDate(), orderItemMembership.getMembership().getEndDate());
            // membershipDto를 생성한다.
            MembershipDto membershipDto = orderMembershipResMapper.toDto(orderItemMembership, membershipUsingPeriod);
            membershipDtos.add(membershipDto);
        }
        // 멤버십 종료 날짜의 내림차순으로 멤버십 이용내역을 반환한다.
        Collections.reverse(membershipDtos);
        return membershipDtos;
    }


    public void yearlyDescriptionRefund(Membership membership) {
        // 월간 구독권인지, 연간 구독권인지 구분 필요
        int period = 0;
        BigDecimal paidPrice = BigDecimal.ZERO;
        if (membership.getMembershipSubscriptionType().equals(MembershipSubscriptionType.YEAR)) {
            period = MembershipUtil.getPeriodWithStartAndEndDate(membership.getStartDate(), LocalDate.now());
            // Day가 일치하지 않으면 1을 추가한다. -> 2022-12-22과 2023-01-21은 0의 결과 값이 나오므로.
            if (membership.getStartDate().getDayOfMonth() > LocalDate.now().getDayOfMonth()) {
                period++;
            }
            paidPrice = paidPrice.subtract(DISCOUNT_YEARLY_MEMBERSHIP_PER_MONTH.multiply(BigDecimal.valueOf(period)));
        }
    }
}
