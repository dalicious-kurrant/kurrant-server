package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.LocationTestDto;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDtoList;
import co.dalicious.domain.food.dto.UpdateMakersReqDto;
import org.locationtech.jts.io.ParseException;

import java.util.List;

public interface MakersService {
    List<MakersInfoResponseDto> findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException;

    void locationTest(LocationTestDto locationTestDto) throws ParseException;

    void updateMakers(UpdateMakersReqDto updateMakersReqDto) throws ParseException;
}
