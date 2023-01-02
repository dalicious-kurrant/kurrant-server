package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.repository.ApartmentRepository;
import co.dalicious.domain.user.dto.MembershipSubscriptionTypeDto;
import co.dalicious.domain.user.entity.MembershipSubscriptionType;
import co.kurrant.app.public_api.service.PublicService;
import co.kurrant.app.public_api.service.impl.mapper.ApartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    private final ApartmentRepository apartmentRepository;
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
    public List<ApartmentResponseDto> getApartments() {
        List<Apartment> apartments = apartmentRepository.findAll();
        List<ApartmentResponseDto> apartmentResponseDtos = new ArrayList<>();
        for(Apartment apartment : apartments) {
            apartmentResponseDtos.add(ApartmentMapper.INSTANCE.toDto(apartment));
        }
        return apartmentResponseDtos;
    }
}
