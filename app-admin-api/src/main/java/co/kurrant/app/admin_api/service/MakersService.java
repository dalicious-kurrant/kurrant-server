package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.*;
import org.locationtech.jts.io.ParseException;

import java.util.List;

public interface MakersService {
    List<MakersInfoResponseDto> findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException;

    void locationTest(LocationTestDto locationTestDto) throws ParseException;

    void updateMakers(SaveMakersRequestDto updateMakersReqDto) throws ParseException;
}
