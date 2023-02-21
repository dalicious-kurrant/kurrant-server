package co.kurrant.app.admin_api.service;

import co.kurrant.app.admin_api.dto.client.SpotResponseDto;

import java.util.List;

public interface SpotService {
    List<SpotResponseDto> getAllSpotList();
}
