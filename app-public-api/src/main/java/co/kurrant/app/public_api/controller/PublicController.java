package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*", allowedHeaders = "*")
@Tag(name = "5. Public")
@RequestMapping(value = "/v1/public")
@RestController
@RequiredArgsConstructor
public class PublicController {
    private final UserService userService;
    @Operation(summary = "멤버십 구독 정보 조회", description = "멤버십 구독 정보를 조회한다.")
    @GetMapping("/membership")
    public ResponseMessage getMembershipSubscriptionInfo() {
        return ResponseMessage.builder()
                .data(userService.getMembershipSubscriptionInfo())
                .message("멤버십 구독 정보 조회에 성공하셨습니다.")
                .build();
    }


}
