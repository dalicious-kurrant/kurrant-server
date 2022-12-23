package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import co.kurrant.app.public_api.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    @Override
    public List<MembershipSubscriptionTypeDto> getMembershipSubscriptionInfo() {
        List<MembershipSubscriptionTypeDto> membershipSubscriptionTypeDtos = new ArrayList<>();

        MembershipSubscriptionTypeDto monthSubscription = MembershipSubscriptionTypeDto.builder()
                .membershipSubscriptionType(MembershipSubscriptionType.MONTH)
                .build();

        MembershipSubscriptionTypeDto yearSubscription = MembershipSubscriptionTypeDto.builder()
                .membershipSubscriptionType(MembershipSubscriptionType.YEAR)
                .build();

        membershipSubscriptionTypeDtos.add(monthSubscription);
        membershipSubscriptionTypeDtos.add(yearSubscription);

        return membershipSubscriptionTypeDtos;
    }
}
