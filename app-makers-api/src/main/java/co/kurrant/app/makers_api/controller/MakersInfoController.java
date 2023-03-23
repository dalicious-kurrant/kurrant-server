package co.kurrant.app.makers_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.OriginDto;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersInfoService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "2. MakersInfo")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/info")
@RestController
public class MakersInfoController {

    private final MakersInfoService makersInfoService;

    @GetMapping("")
    public ResponseMessage getMakersInfo(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(makersInfoService.getMakersInfo(securityUser))
                .message("메이커스 정보 조회에 성공하였습니다.")
                .build();
    }

    @GetMapping("/origins")
    public ResponseMessage getMakersOrigins(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(makersInfoService.getMakersOrigins(securityUser))
                .message("메이커스 원산지 조회에 성공하였습니다.")
                .build();
    }

    @PostMapping("/origins")
    public ResponseMessage postMakersOrigins(Authentication authentication, @RequestBody List<OriginDto> originDtos) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        makersInfoService.postMakersOrigins(securityUser, originDtos);
        return ResponseMessage.builder()
                .message("메이커스 원산지 추가에 성공하였습니다.")
                .build();
    }

    @PatchMapping("/origins")
    public ResponseMessage updateMakersOrigin(Authentication authentication,@RequestBody OriginDto.WithId originDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        makersInfoService.updateMakersOrigin(securityUser, originDto);
        return ResponseMessage.builder()
                .message("메이커스 원산지 수정에 성공하였습니다.")
                .build();
    }
}
