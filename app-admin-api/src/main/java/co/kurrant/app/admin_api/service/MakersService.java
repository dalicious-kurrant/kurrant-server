package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDto;

public interface MakersService {
    Object findAllMakersInfo();

    void saveMakers(SaveMakersRequestDto saveMakersRequestDto);
}
