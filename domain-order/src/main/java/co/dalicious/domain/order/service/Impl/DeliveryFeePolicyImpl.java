package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.client.entity.enums.DeliveryFeeOption;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DeliveryFeePolicyImpl implements DeliveryFeePolicy {

    private static final BigDecimal DELIVERY_FEE = BigDecimal.valueOf(3500L);

    @Override
    public BigDecimal getDeliveryFee() {
        return DELIVERY_FEE;
    }

    @Override
    public BigDecimal getGroupDeliveryFee(User user, Group group) {
        group = (Group) Hibernate.unproxy(group);
        if (group instanceof Corporation corporation && corporation.getDeliveryFeeOption().equals(DeliveryFeeOption.PERSONAL)) {
            return getUserDeliveryFee(user);
        } else if (group instanceof Corporation corporation) {
            return getCorporationDeliveryFee(user, corporation);
        } else if (group instanceof OpenGroup openGroup) {
            return getOpenGroupDeliveryFee(user, openGroup);
        }
        return getUserDeliveryFee(user);
    }

    @Override
    public BigDecimal getUserDeliveryFee(User user) {
        Boolean isMembership = user.getIsMembership();
        if (isMembership) {
            return getMembershipDeliveryFee();
        } else {
            return getNoMembershipDeliveryFee();
        }
    }

    @Override
    public BigDecimal getOpenGroupDeliveryFee(User user, OpenGroup openGroup) {
        Boolean isMembership = user.getIsMembership();
        if (isMembership) {
            return getMembershipOpenGroupDeliveryFee();
        } else {
            return getNoMembershipOpenGroupDeliveryFee();
        }
    }

    @Override
    public BigDecimal getCorporationDeliveryFee(User user, Corporation corporation) {
        // TODO: 정산시 사용, 앱에서는 0원으로 지정
        if (corporation.getIsMembershipSupport() || !corporation.getIsMembershipSupport()) {
            return getMembershipCorporationDeliveryFee();
        } else if (corporation.getEmployeeCount() >= 50) {
            return getNoMembershipCorporationDeliveryFeeUpper50(corporation);
        } else if (corporation.getEmployeeCount() > 0) {
            return getNoMembershipCorporationDeliveryFeeLower50();
        }
        throw new ApiException(ExceptionEnum.IS_NOT_APPROPRIATE_EMPLOYEE_COUNT);
    }

    @Override
    public BigDecimal getMembershipDeliveryFee() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNoMembershipDeliveryFee() {
        return DELIVERY_FEE;
    }

    @Override
    public BigDecimal getMembershipOpenGroupDeliveryFee() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNoMembershipOpenGroupDeliveryFee() {
        return DELIVERY_FEE;
    }

    @Override
    public BigDecimal getMembershipCorporationDeliveryFee() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNoMembershipCorporationDeliveryFeeLower50() {
        return DELIVERY_FEE;
    }

    @Override
    public BigDecimal getNoMembershipCorporationDeliveryFeeUpper50(Corporation corporation) {
        String strAddress = corporation.getAddress().getAddress1();
        // 강남 3구, 강남, 서초, 송파
        if (strAddress.contains("강남구") || strAddress.contains("서초구") || strAddress.contains("송파구")) {
            return BigDecimal.valueOf(20000L);
        }
        // 서울
        if (strAddress.contains("서울시")) {
            return BigDecimal.valueOf(25000L);
        }
        // 그 외
        return BigDecimal.valueOf(25000L);
    }

    @Override
    public BigDecimal getNoMembershipExistedCorporationDeliveryFeeUpper50(Address address) {
        String strAddress = address.getAddress1();
        // 강남 3구, 강남, 서초, 송파
        if (strAddress.contains("강남구") || strAddress.contains("서초구") || strAddress.contains("송파구")) {
            return BigDecimal.valueOf(15000L);
        }
        // 서울
        if (strAddress.contains("서울시")) {
            return BigDecimal.valueOf(20000L);
        }
        // 그 외
        return BigDecimal.valueOf(25000L);
    }
}
