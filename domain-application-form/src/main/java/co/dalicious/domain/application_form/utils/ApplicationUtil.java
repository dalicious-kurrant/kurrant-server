package co.dalicious.domain.application_form.utils;

import co.dalicious.domain.application_form.dto.requestMySpotZone.publicApp.RequestedMySpotDto;
import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.application_form.mapper.RequestedMySpotMapper;
import co.dalicious.domain.application_form.repository.QRequestedMySpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ApplicationUtil {
    private final QRequestedMySpotRepository qRequestedMySpotRepository;
    private final RequestedMySpotMapper requestedMySpotMapper;
    // TODO: (지성님) UserInfo에서 MySpot 신청 정보 받아오는 것 위치 고려
    public RequestedMySpotDto findExistRequestedMySpot(BigInteger userId) {
        RequestedMySpot requestedMySpot = qRequestedMySpotRepository.findRequestedMySpotByUserId(userId);
        return requestedMySpotMapper.toRequestedMySpotDto(requestedMySpot);
    }
}
