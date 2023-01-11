package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.dto.client.ClientSpotDetailReqDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserClientService;
import co.kurrant.app.public_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name = "6. ClientType")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/clients")
@RestController
public class ClientController {
    private final UserClientService userClientService;
    private final UserService userService;

    @Operation(summary = "유저가 속한 그룹의 정보 리스트", description = "유저가 속한 그룹의 정보 리스트를 조회한다.")
    @GetMapping("")
    public ResponseMessage getClients(Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return ResponseMessage.builder()
                .data(userService.getClients(securityUser))
                .message("그룹 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹별 스팟 상세 조회", description = "유저가 속한 그룹의 스팟들의 상세 정보를 조회한다.")
    @GetMapping("/spots/{spotId}")
    public ResponseMessage getSpotDetail(Authentication authentication,
                                         @PathVariable BigInteger spotId) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return ResponseMessage.builder()
                .data(userClientService.getSpotDetail(securityUser, spotId))
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스팟 등록", description = "유저의 스팟을 등록한다.")
    @PostMapping("/spots/{spotId}")
    public ResponseMessage saveUserSpot(Authentication authentication,
                                        @PathVariable BigInteger spotId,
                                        @RequestBody(required = false) ClientSpotDetailReqDto spotDetailReqDto) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return ResponseMessage.builder()
                .data(userClientService.saveUserSpot(securityUser, spotDetailReqDto, spotId))
                .message("스팟 등록에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹 탈퇴", description = "유저가 속한 그룹에서 나간다.")
    @PostMapping("/{groupId}")
    public ResponseMessage withdrawClient(Authentication authentication,
                                          @PathVariable BigInteger groupId) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return ResponseMessage.builder()
                .data(userClientService.withdrawClient(securityUser, groupId))
                .message("그룹 탈퇴에 성공하였습니다.")
                .build();
    }
}
