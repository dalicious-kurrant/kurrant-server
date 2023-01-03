package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "6. ClientType")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/clients")
@RestController
public class ClientController {
    @Operation(summary = "유저가 속한 그룹의 정보 리스트", description = "유저가 속한 그룹의 정보 리스트를 조회한다.")
    @GetMapping("")
    public ResponseMessage getClients(HttpServletRequest httpServletRequest) {
        return ResponseMessage.builder()
                .message("그룹 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹별 스팟 상세 조회", description = "유저가 속한 그룹의 스팟들의 상세 정보를 조회한다.")
    @GetMapping("/spots")
    public ResponseMessage getSpotDetail(HttpServletRequest httpServletRequest,
                                         @RequestParam("clientType") Integer clientType,
                                         @RequestParam("clientId") Integer clientId,
                                         @RequestParam("spotId") Integer spotId) {
        return ResponseMessage.builder()
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스팟 등록", description = "유저의 스팟을 등록한다.")
    @PostMapping("/spots")
    public ResponseMessage saveUserSpot(HttpServletRequest httpServletRequest,
                                         @RequestParam("clientType") Integer clientType,
                                         @RequestParam("clientId") Integer clientId,
                                         @RequestParam("spotId") Integer spotId) {
        return ResponseMessage.builder()
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹 탈퇴", description = "유저가 속한 그룹에서 나간다.")
    @PostMapping("")
    public ResponseMessage withdrawClient(HttpServletRequest httpServletRequest,
                                            @RequestParam("clientType") Integer clientType,
                                            @RequestParam("clientId") Integer clientId) {
        return ResponseMessage.builder()
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }
}
