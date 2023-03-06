package co.dalicious.domain.order.service;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.entity.OpenGroup;
import co.dalicious.domain.user.entity.User;

import java.math.BigDecimal;

public interface DeliveryFeePolicy {
    BigDecimal getDeliveryFee();
    BigDecimal getGroupDeliveryFee(User user, Group group);
    BigDecimal getApartmentUserDeliveryFee(User user, Apartment apartment);
    BigDecimal getOpenGroupDeliveryFee(User user, OpenGroup openGroup);
    BigDecimal getCorporationDeliveryFee(User user, Corporation corporation);
    // 그룹이 아파트이고, 멤버십을 구매한 경우
    BigDecimal getMembershipApartmentDeliveryFee();
    // 그룹이 아파트이고, 멤버십을 구매하지 않은 경우
    BigDecimal getNoMembershipApartmentDeliveryFee();
    // 그룹이 오픈 그룹이고, 멤버십을 구매한 경우
    BigDecimal getMembershipOpenGroupDeliveryFee();
    // 그룹이 오픈 그룹이고, 멤버십을 구매하지 않은 경우
    BigDecimal getNoMembershipOpenGroupDeliveryFee();
    // 그룹이 기업이고, 기업이 멤버십을 지원할 경우
    BigDecimal getMembershipCorporationDeliveryFee();
    // 그룹이 기업이고, 기업이 멤버십을 지원하지 않으며, 기업 인원이 50명 미만일 경우
    BigDecimal getNoMembershipCorporationDeliveryFeeLower50();
    // 그룹이 기업이고, 기업이 멤버십을 지원하지 않으며, 기업 인원이 50명 이상일 경우
    BigDecimal getNoMembershipCorporationDeliveryFeeUpper50(Address address);
    // 그룹이 기업이고, 기업이 멤버십을 지원하지 않으며, 기존게 가입된 고객사이며, 기업 인원이 50명 이상일 경우
    BigDecimal getNoMembershipExistedCorporationDeliveryFeeUpper50(Address address);
}
