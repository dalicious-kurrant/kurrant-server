package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.LocationTestDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDtoList;
import org.locationtech.jts.io.ParseException;

public interface MakersService {
    Object findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException;

    void locationTest(LocationTestDto locationTestDto) throws ParseException;
}
