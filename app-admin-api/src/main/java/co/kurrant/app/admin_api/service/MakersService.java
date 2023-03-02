package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDto;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDtoList;
import org.locationtech.jts.io.ParseException;

public interface MakersService {
    Object findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException;
}
