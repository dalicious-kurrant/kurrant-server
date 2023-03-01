package co.kurrant.app.makers_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.food.dto.PresetScheduleRequestDto;
import co.dalicious.domain.food.dto.PresetScheduleResponseDto;
import co.kurrant.app.makers_api.model.SecurityUser;

import java.util.List;

public interface ScheduleService {
    ListItemResponseDto<PresetScheduleResponseDto> getMostRecentPresets(Integer limit, Integer page, OffsetBasedPageRequest pageable, SecurityUser securityUser);
    void updateScheduleStatus(SecurityUser securityUser, PresetScheduleRequestDto requestDto);

}
