package co.kurrant.app.public_api.controller.user;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.order.dto.OrderMembershipReqDto;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.UserUtil;
import co.kurrant.app.public_api.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Membership")
@RequestMapping(value = "/v1/users/membership")
@RestController
@RequiredArgsConstructor
public class MembershipController {
    private final UserUtil userUtil;
    private final MembershipService membershipService;
    @Operation(summary = "멤버십 이용내역", description = "유저의 멤버십 이용 내역을 조회한다.")
    @GetMapping("")
    public ResponseMessage retrieveMembership(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(membershipService.retrieveMembership(securityUser))
                .message("멤버십 이용 내역 조회에 성공하셨습니다.")
                .build();
    }



    @Operation(summary = "멤버십 구매", description = "유저가 멤버십에 가입한다")
    @PostMapping("")
    public ResponseMessage joinMembership(Authentication authentication, @RequestBody OrderMembershipReqDto orderMembershipReqDto) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        membershipService.joinMembership(securityUser, orderMembershipReqDto);
        return ResponseMessage.builder()
                .message("멤버십 구매에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "멤버십 주문 정보 가져오기", description = "유저가 멤버십 주문 정보를 불러온다.")
    @GetMapping("/{subscriptionType}")
    public ResponseMessage getOrderMembership(Authentication authentication, @PathVariable Integer subscriptionType) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .data(membershipService.getOrderMembership(securityUser, subscriptionType))
                .message("멤버십 주문 정보 가져오기에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "멤버십 해지/환불", description = "유저가 멤버십을 해지 또는 환불한다")
    @PostMapping("/unsubscribing")
    public ResponseMessage unsubscribingMembership(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        membershipService.unsubscribeMembership(securityUser);
        return ResponseMessage.builder()
                .message("멤버십 해지/환불에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "멤버십 혜택 금액 가져오기", description = "유저가 멤버십을 이용하는 동안 받았던 혜택 금액을 조회한다.")
    @GetMapping("/benefits")
    public void getPriceBenefits(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
    }

    @Operation(summary = "멤버십 자동 결제 수단 저장하기", description = "유저가 멤버십을 자동 결제할 시 사용할 결제 수단을 정한다.")
    @PostMapping("/paymentType")
    public void saveMembershipAutoPayment(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
    }
}
