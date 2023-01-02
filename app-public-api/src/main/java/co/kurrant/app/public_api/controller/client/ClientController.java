package co.kurrant.app.public_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "6. Client")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users/me/clients")
@RestController
public class ClientController {
    @Operation(summary = "유저가 속한 그룹의 정보 리스트", description = "유저가 속한 그룹의 정보 리스트를 조회한다.")
    @GetMapping("/pre-registration")
    public ResponseMessage getClients(HttpServletRequest httpServletRequest) {
        return ResponseMessage.builder()
                .message("그룹 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "유저가 속한 그룹의 스팟 리스트", description = "유저가 속한 그룹의 스팟 리스트를 조회한다.")
    @GetMapping("/{client}/{clientId}")
    public ResponseMessage getSpots(HttpServletRequest httpServletRequest, @PathVariable String client, @PathVariable Integer clientId) {
        return ResponseMessage.builder()
                .message("그룹 스팟 조회에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "그룹별 스팟 상세 조회", description = "유저가 속한 그룹의 스팟들의 상세 정보를 조회한다.")
    @GetMapping("/{client}/spots/{spotId}")
    public ResponseMessage getSpotDetail(HttpServletRequest httpServletRequest, @PathVariable Integer client, @PathVariable Integer spotId) {
        return ResponseMessage.builder()
                .message("스팟 상세 조회에 성공하였습니다.")
                .build();
    }
}
