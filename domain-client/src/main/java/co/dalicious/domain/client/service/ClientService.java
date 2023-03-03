package co.dalicious.domain.client.service;

import co.dalicious.domain.client.dto.ApartmentRequestDto;
import co.dalicious.domain.client.dto.CorporationRequestDto;
import org.locationtech.jts.io.ParseException;

public interface ClientService {
    // 아파트 그룹을 생성한다.
    void createApartment(ApartmentRequestDto apartmentRequestDto) throws ParseException;
    // 기업 그룹을 생성한다.
    void createCorporation(CorporationRequestDto corporationRequestDto) throws ParseException;

}
