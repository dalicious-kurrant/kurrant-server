package co.dalicious.domain.user.service;

import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.user.entity.User;

import java.util.List;

public interface UserService {
    // 멤버십 구독 정보를 가져온다.
    List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo();
    // 유저가 속한 그룹 정보 리스트
    List<SpotListResponseDto> getClients(User user);
}
