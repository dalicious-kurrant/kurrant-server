package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDto;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDtoList;

public interface MakersService {
    Object findAllMakersInfo();

    void saveMakers(SaveMakersRequestDtoList saveMakersRequestDtoList);
}
