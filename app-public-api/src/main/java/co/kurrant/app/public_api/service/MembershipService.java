package co.kurrant.app.public_api.service;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.user.entity.Membership;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.dto.user.MembershipDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MembershipService {
    // 유저의 전체 멤버십 내용을 조회한다.
    List<MembershipDto> retrieveMembership(HttpServletRequest httpServletRequest);

    // 유저가 멤버십을 자동 결제할 시 사용할 결제 수단을 정한다.
    void saveMembershipAutoPayment(HttpServletRequest httpServletRequest);
}
