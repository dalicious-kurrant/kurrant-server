package co.kurrant.app.admin_api.service;

import co.dalicious.domain.client.dto.SpotResponseDto;
import co.kurrant.app.admin_api.dto.client.DeleteSpotRequestDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;

import java.util.List;

public interface SpotService {
    List<SpotResponseDto> getAllSpotList();

    void saveSpotList(SaveSpotList saveSpotList);

    void deleteSpot(DeleteSpotRequestDto deleteSpotRequestDto);
}
