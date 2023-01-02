package co.kurrant.app.public_api.service;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;

import java.util.List;

public interface PublicService {
    // 멤버십 구독 정보를 가져온다.
    List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo();
    // 고객사로 등록된 아파트 전체 리스트를 불러온다.
    List<ApartmentResponseDto> getApartments();
}
