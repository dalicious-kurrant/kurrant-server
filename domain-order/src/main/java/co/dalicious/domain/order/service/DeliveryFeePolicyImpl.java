package co.dalicious.domain.order.service;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.order.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DeliveryFeePolicyImpl implements DeliveryFeePolicy{
    @Override
    public BigDecimal getUserDeliveryFee(Order order) {
        return null;
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
        // 강남 3구
        // 서울
        // 그 외
        return null;
    }
}
