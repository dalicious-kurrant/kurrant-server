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
    public RequestedMySpotDto findExistRequestedMySpot(BigInteger userId) {
        RequestedMySpot requestedMySpot = qRequestedMySpotRepository.findRequestedMySpotByUserId(userId);
        return requestedMySpotMapper.toRequestedMySpotDto(requestedMySpot);
    }
}
