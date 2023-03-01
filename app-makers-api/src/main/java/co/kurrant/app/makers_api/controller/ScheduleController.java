package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.PresetScheduleRequestDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.ScheduleService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="*", allowedHeaders = "*")
@RequestMapping(value = "/v1/makers/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation
    @GetMapping("")
    public ResponseMessage getMostRecentPresets(Authentication authentication, @RequestParam Integer page, @RequestParam Integer limit, OffsetBasedPageRequest pageable) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("일정관리를 조회했습니다.")
                .data(scheduleService.getMostRecentPresets(limit, page, pageable, securityUser))
                .build();
    }

    @Operation
    @PostMapping("")
    public ResponseMessage updateScheduleStatus(Authentication authentication, @RequestBody PresetScheduleRequestDto requestDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        scheduleService.updateScheduleStatus(securityUser, requestDto);
        return ResponseMessage.builder()
                .message("일정관리를 수정했습니다.")
                .build();
    }
}

