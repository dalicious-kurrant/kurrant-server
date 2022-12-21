package co.kurrant.app.public_api.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "3. Membership")
@RequestMapping(value = "/v1/users/membership")
@RestController
@RequiredArgsConstructor
public class MembershipController {
    @Operation(summary = "멤버십 이용내역", description = "유저의 멤버십 이용 내역을 조회한다.")
    @GetMapping("/")
    public void retrieveMembership(HttpServletRequest httpServletRequest) {
    }

    @Operation(summary = "멤버십 구매", description = "유저가 멤버십에 가입한다")
    @PostMapping("/{subscriptionType}")
    public void joinMembership(HttpServletRequest httpServletRequest, @PathVariable String subscriptionType) {
    }

    @Operation(summary = "멤버십 해지/환불", description = "유저가 멤버십을 해지 또는 환불한다")
    @PostMapping("/unsubscribing")
    public void unsubscribingMembership(HttpServletRequest httpServletRequest) {
    }

    @Operation(summary = "멤버십 혜택 금액 가져오기", description = "유저가 멤버십을 이용하는 동안 받았던 혜택 금액을 조회한다.")
    @GetMapping("/benefits")
    public void getPriceBenefits(HttpServletRequest httpServletRequest) {
    }

    @Operation(summary = "멤버십 자동 결제 수단 저장하기", description = "유저가 멤버십을 자동 결제할 시 사용할 결제 수단을 정한다.")
    @PostMapping("/paymentType")
    public void saveMembershipAutoPayment(HttpServletRequest httpServletRequest) {
    }
}
