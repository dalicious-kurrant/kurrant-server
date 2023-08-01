package co.kurrant.app.public_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.MembershipService;
import co.kurrant.app.public_api.service.UserService;
import co.kurrant.app.public_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin(origins="*", allowedHeaders = "*")
@Tag(name = "멤버십")
@RequestMapping(value = "/v1/public")
@RestController
@RequiredArgsConstructor
public class PublicController {
    private final MembershipService membershipService;
    private final UserService userService;
    @Operation(summary = "멤버십 구독 정보 조회", description = "멤버십 구독 정보를 조회한다.")
    @GetMapping("/membership")
    public ResponseMessage getMembershipSubscriptionInfo(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(membershipService.getMembershipSubscriptionInfo(securityUser))
                .message("멤버십 구독 정보 조회에 성공하셨습니다.")
                .build();
    }

    @Operation(summary = "랜덤 닉네임 생성", description = "랜덤 닉네임을 생성한다.")
    @GetMapping("/nicknames")
    public ResponseMessage generateRandomNickname(Authentication authentication) throws IOException {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(userService.generateRandomNickName(securityUser))
                .message("랜덤 닉네임 생성에 성공하셨습니다.")
                .build();
    }
}
