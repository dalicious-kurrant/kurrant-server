package co.dalicious.domain.client.service;

import co.dalicious.domain.client.dto.ApartmentRequestDto;
import co.dalicious.domain.client.dto.ApartmentResponseDto;
import co.dalicious.domain.client.dto.CorporationRequestDto;
import org.locationtech.jts.io.ParseException;

import java.util.List;

public interface ClientService {
    // 아파트 그룹을 생성한다.
    void createApartment(ApartmentRequestDto apartmentRequestDto);
    // 기업 그룹을 생성한다.
    void createCorporation(CorporationRequestDto corporationRequestDto);

}
