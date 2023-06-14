package co.dalicious.domain.application_form.utils;

import co.dalicious.domain.application_form.entity.RequestedMySpot;
import co.dalicious.domain.application_form.repository.QRequestedMySpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ApplicationUtil {
    private final QRequestedMySpotRepository qRequestedMySpotRepository;

    public RequestedMySpot findExistRequestedMySpot(BigInteger userId) {
        return qRequestedMySpotRepository.findRequestedMySpotByUserId(userId);
    }
}
