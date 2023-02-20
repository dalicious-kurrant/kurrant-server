package co.kurrant.app.makers_api.service;

import co.dalicious.domain.food.dto.PresetScheduleRequestDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.List;

public interface ScheduleService {
    List<PresetScheduleResponseDto> getMostRecentPresets(Integer page, SecurityUser securityUser);
    void updateScheduleStatus(SecurityUser securityUser, PresetScheduleRequestDto requestDto);

}
