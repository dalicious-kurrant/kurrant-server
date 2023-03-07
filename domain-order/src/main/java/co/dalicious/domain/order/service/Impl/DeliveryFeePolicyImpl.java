package co.dalicious.domain.order.service.Impl;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.order.service.DeliveryFeePolicy;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
        if(group instanceof Apartment) {
            return getApartmentUserDeliveryFee(user, (Apartment) group);
        } else if (group instanceof Corporation) {
            // TODO: 추후 삭제
            if(group.getName().replaceAll("\\s+", "").contains("스파크플러스") && LocalDate.now().isBefore(LocalDate.of(2023, 4, 1))) {
                return getMembershipCorporationDeliveryFee();
            }
            return getCorporationDeliveryFee(user, (Corporation) group);
        } else if (group instanceof OpenGroup) {
            return getOpenGroupDeliveryFee(user, (OpenGroup) group);
        }
        throw new ApiException(ExceptionEnum.NOT_FOUND);
    }

    @Override
    public BigDecimal getApartmentUserDeliveryFee(User user, Apartment apartment) {
        Boolean isMembership = user.getIsMembership();
        if (isMembership) {
            return getMembershipApartmentDeliveryFee();
        } else {
            return getNoMembershipApartmentDeliveryFee();
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
            return getNoMembershipCorporationDeliveryFeeUpper50(corporation.getAddress());
        } else if (corporation.getEmployeeCount() > 0) {
            return getNoMembershipCorporationDeliveryFeeLower50();
        }
        throw new ApiException(ExceptionEnum.IS_NOT_APPROPRIATE_EMPLOYEE_COUNT);
    }

    @Override
    public BigDecimal getMembershipApartmentDeliveryFee() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNoMembershipApartmentDeliveryFee() {
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
    public BigDecimal getNoMembershipCorporationDeliveryFeeUpper50(Address address) {
        String strAddress = address.getAddress1();
        // 강남 3구, 강남, 서초, 송파
        if (strAddress.contains("강남구") || strAddress.contains("서초구") || strAddress.contains("송파구")) {
            return BigDecimal.valueOf(20000L);
        }
        // 서울
        if (strAddress.contains("서울시")) {
            return BigDecimal.valueOf(25000L);
        }
        // 그 외
        return BigDecimal.valueOf(30000L);
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
