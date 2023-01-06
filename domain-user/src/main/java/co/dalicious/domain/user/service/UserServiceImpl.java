package co.dalicious.domain.user.service;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.client.dto.SpotListResponseDto;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserApartment;
import co.dalicious.domain.user.entity.UserCorporation;
import co.dalicious.domain.user.entity.enums.MembershipSubscriptionType;
import co.dalicious.domain.client.mapper.ApartmentResponseMapper;
import co.dalicious.domain.client.mapper.CorporationResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final ApartmentResponseMapper apartmentResponseMapper;
    private final CorporationResponseMapper corporationResponseMapper;
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

    @Override
    @Transactional
    public List<SpotListResponseDto> getClients(User user) {
        // 그룹/스팟 정보 가져오기
        List<UserApartment> userApartments = user.getApartments();
        List<UserCorporation> userCorporations = user.getCorporations();
        // 그룹/스팟 리스트를 담아줄 Dto 생성하기
        List<SpotListResponseDto> spotListResponseDtoList = new ArrayList<>();
        // 그룹: 아파트 추가
        for (UserApartment userApartment : userApartments) {
            Apartment apartment = userApartment.getApartment();
            spotListResponseDtoList.add(apartmentResponseMapper.toDto(apartment));
        }
        // 그룹: 기업 추가
        for (UserCorporation userCorporation : userCorporations) {
            Corporation corporation = userCorporation.getCorporation();
            spotListResponseDtoList.add(corporationResponseMapper.toDto(corporation));
        }
        return spotListResponseDtoList;
    }
}
