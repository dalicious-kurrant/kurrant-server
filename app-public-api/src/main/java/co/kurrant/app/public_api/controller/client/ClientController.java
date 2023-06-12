package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.GroupAndSpotIdReqDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@Tag(name = "6. Group")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/groups")
@RestController
public class ClientController {
    private final UserClientService userClientService;
    private final UserService userService;

    @Operation(summary = "유저가 속한 그룹의 정보 리스트", description = "유저가 속한 그룹의 정보 리스트를 조회한다.")
    @GetMapping("")
    public ResponseMessage getClients(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userService.getClients(securityUser))
                .message("그룹 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "등록된 오픈 그룹 전체 조회", description = "고객사로 등록된 오픈 그룹을 전체를 조회한다.")
    @GetMapping("/spots/share")
    public ResponseMessage getOpenGroups(Authentication authentication,
                                                      @RequestParam Map<String, Object> location,
                                                      @RequestParam(required = false) Map<String, Object> parameters,
                                                      @RequestParam(required = false, defaultValue = "20") Integer limit, @RequestParam Integer page) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        OffsetBasedPageRequest pageable = new OffsetBasedPageRequest(((long) limit * (page - 1)), limit, Sort.unsorted());
        return ResponseMessage.builder()
                .data(userClientService.getOpenGroups(securityUser, location, parameters, pageable))
                .message("오픈 그룹 전체 조회에 성공하셨습니다.")
                .build();
    }

    @Operation(summary = "고객사로 등록된 오픈 그룹/아파트 선택", description = "고객사로 등록된 오픈 그룹/아파트를 그룹에 추가한다.")
    @PostMapping("/spots/share")
    public ResponseMessage settingOpenGroup(Authentication authentication, @RequestBody GroupAndSpotIdReqDto groupAndSpotIdReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.settingOpenGroup(securityUser, groupAndSpotIdReqDto.getId());
        return ResponseMessage.builder()
                .message("유저 그룹(기업) 설정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스팟 선택", description = "유저의 기본 스팟을 등록한다.")
    @PostMapping("/spots")
    public ResponseMessage selectUserSpot(Authentication authentication,
                                          @RequestBody GroupAndSpotIdReqDto groupAndSpotIdReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        BigInteger result = userClientService.selectUserSpot(securityUser, groupAndSpotIdReqDto.getId());
        return ResponseMessage.builder()
                .data(result)
                .message((result == null) ? "스팟 상세 주소 등록이 필요합니다" : "스팟 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹별 스팟 상세 조회", description = "유저가 속한 그룹의 스팟들의 상세 정보를 조회한다.")
    @GetMapping("/spots/{spotId}")
    public ResponseMessage getSpotDetail(Authentication authentication,
                                         @PathVariable BigInteger spotId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.getSpotDetail(securityUser, spotId))
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹 탈퇴", description = "유저가 속한 그룹에서 나간다.")
    @PostMapping("")
    public ResponseMessage withdrawClient(Authentication authentication,
                                          @RequestBody GroupAndSpotIdReqDto groupAndSpotIdReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.withdrawClient(securityUser, groupAndSpotIdReqDto.getId()))
                .message("그룹 탈퇴에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "등록된 오픈 그룹 상세 조회", description = "고객사로 등록된 오픈 그룹을 상세 조회한다.")
    @GetMapping("/spots/share/{groupId}")
    public ResponseMessage getOpenSpotDetail(Authentication authentication, @PathVariable BigInteger groupId) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.getOpenSpotDetail(securityUser, groupId))
                .message("오픈 그룹 상세 조회에 성공하셨습니다.")
                .build();
    }
}
