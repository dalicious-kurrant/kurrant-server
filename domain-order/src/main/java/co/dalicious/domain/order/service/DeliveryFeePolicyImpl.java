package co.dalicious.domain.order.service;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.entity.User;
import exception.ApiException;
import exception.ExceptionEnum;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DeliveryFeePolicyImpl implements DeliveryFeePolicy {

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
    public BigDecimal getCorporationDeliveryFee(User user, Corporation corporation) {
        if (corporation.getIsMembershipSupport()) {
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
        return BigDecimal.valueOf(3500L);
    }

    @Override
    public BigDecimal getMembershipCorporationDeliveryFee() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getNoMembershipCorporationDeliveryFeeLower50() {
        return BigDecimal.valueOf(3500L);
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
