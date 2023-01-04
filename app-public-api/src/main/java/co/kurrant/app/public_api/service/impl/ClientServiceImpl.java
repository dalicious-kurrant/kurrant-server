package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserApartment;
import co.dalicious.domain.user.entity.UserCorporation;
import co.kurrant.app.public_api.dto.client.SpotListResponseDto;
import co.kurrant.app.public_api.service.ClientService;
import co.kurrant.app.public_api.service.CommonService;
import co.kurrant.app.public_api.service.impl.mapper.client.ApartmentResponseMapper;
import co.kurrant.app.public_api.service.impl.mapper.client.CorporationResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final CommonService commonService;

    @Override
    @Transactional
    public List<SpotListResponseDto> getClients(HttpServletRequest httpServletRequest) {
        // 유저 정보 가져오기
        User user = commonService.getUser(httpServletRequest);
        // 그룹/스팟 정보 가져오기
        List<UserApartment> userApartments = user.getApartments();
        List<UserCorporation> userCorporations = user.getCorporations();
        // 그룹/스팟 리스트를 담아줄 Dto 생성하기
        List<SpotListResponseDto> spotListResponseDtoList = new ArrayList<>();
        // 그룹: 아파트 추가
        for (UserApartment userApartment : userApartments) {
            Apartment apartment = userApartment.getApartment();
            spotListResponseDtoList.add(ApartmentResponseMapper.INSTANCE.toDto(apartment));
        }
        // 그룹: 기업 추가
        for (UserCorporation userCorporation : userCorporations) {
            Corporation corporation = userCorporation.getCorporation();
            spotListResponseDtoList.add(CorporationResponseMapper.INSTANCE.toDto(corporation));
        }
        return spotListResponseDtoList;
    }

    @Override
    public void getSpotDetail(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId) {

    }

    @Override
    public void saveUserSpot(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId, Integer spotId) {

    }

    @Override
    public void withdrawClient(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId) {

    }

    @Override
    public void setUserGroup(HttpServletRequest httpServletRequest, Integer clientType, Integer clientId) {

    }
}
