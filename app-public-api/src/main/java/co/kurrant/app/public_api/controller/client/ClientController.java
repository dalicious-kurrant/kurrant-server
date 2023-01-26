package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.GroupAndSpotIdReqDto;
import co.dalicious.domain.client.dto.ClientSpotDetailReqDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.controller.food.service.UserClientService;
import co.kurrant.app.public_api.controller.food.service.UserService;
import co.kurrant.app.public_api.controller.food.service.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

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

    @Operation(summary = "고객사로 등록된 아파트 전체 조회", description = "고객사로 등록된 아파트들 전체를 조회한다.")
    @GetMapping("/apartments")
    public ResponseMessage getApartments(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.getApartments(securityUser))
                .message("아파트 전체 조회에 성공하셨습니다.")
                .build();
    }

    @PostMapping("/apartments")
    public ResponseMessage settingGroup(Authentication authentication, @RequestBody GroupAndSpotIdReqDto groupAndSpotIdReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        userService.settingGroup(securityUser, groupAndSpotIdReqDto.getId());
        return ResponseMessage.builder()
                .message("유저 그룹(기업) 설정에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "아파트 스팟 선택", description = "유저의 기본 스팟을 등록한다.")
    @PostMapping("/apartments/spots/{spotId}")
    public ResponseMessage saveUserSpot(Authentication authentication,
                                        @PathVariable BigInteger spotId,
                                        @RequestBody(required = false) ClientSpotDetailReqDto spotDetailReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.saveUserDefaultSpot(securityUser, spotDetailReqDto, spotId))
                .message("스팟 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "아파트 스팟 상세주소 변경", description = "유저 스팟의 상세주소를 변경한다..")
    @PutMapping("/apartments/spots/{spotId}")
    public ResponseMessage updateUserHo(Authentication authentication,
                                        @PathVariable BigInteger spotId,
                                        @RequestBody(required = false) ClientSpotDetailReqDto spotDetailReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userClientService.saveUserDefaultSpot(securityUser, spotDetailReqDto, spotId))
                .message("스팟 등록에 성공하였습니다.")
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
}
