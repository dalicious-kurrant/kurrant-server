package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.*;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MakersService {
    List<SaveMakersRequestDto> findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException;

    void locationTest(LocationTestDto locationTestDto) throws ParseException;

    void updateMakers(SaveMakersRequestDto updateMakersReqDto, List<MultipartFile> files) throws ParseException, IOException;
}
