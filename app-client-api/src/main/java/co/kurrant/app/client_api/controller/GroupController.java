package co.kurrant.app.client_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.client_api.dto.UpdateNameDto;
import co.kurrant.app.client_api.dto.UpdatePhoneDto;
import co.kurrant.app.client_api.model.SecurityUser;
import co.kurrant.app.client_api.service.GroupService;
import co.kurrant.app.client_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@Tag(name = "3. Group")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/groups")
public class GroupController {

    public final GroupService groupService;

    @Operation(summary = "기업 정보 조회", description = "기업 정보를 조회합니다. 이름으로 필터링 할 수 있습니다.")
    @GetMapping("")
    public ResponseMessage getGroupList(Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("기업 정보를 조회했습니다.")
                .data(groupService.getGroupInfo(securityUser))
                .build();
    }

    @Operation(summary = "기업 스팟 정보 조회", description = "기업 상세 스팟을 조회한다.")
    @GetMapping("/{groupId}/spots")
    public ResponseMessage getSpotList(@PathVariable BigInteger groupId, Authentication authentication) {
        SecurityUser securityUser = UserUtil.securityUser(authentication);
        return ResponseMessage.builder()
                .message("기업 상세 스팟을 조회했습니다.")
                .data(groupService.getSpots(groupId, securityUser))
                .build();
    }

    @Operation(summary = "기업 담당자 이름 수정", description = "기업 담장자 이름을 수정합니다.")
    @PatchMapping("/name")
    public ResponseMessage updateGroupManagerName(Authentication authentication, @RequestBody UpdateNameDto nameDto) {
        groupService.updateGroupManagerName(UserUtil.securityUser(authentication), nameDto);
        return ResponseMessage.builder()
                .message("기업 담당자 이름 수정을 성공했습니다.")
                .build();
    }

    @Operation(summary = "기업 번호 수정", description = "기업 담당자의 번호를 수정합니다.")
    @PatchMapping("/phone")
    public ResponseMessage updateGroupManagerPhone(Authentication authentication, @RequestBody UpdatePhoneDto phoneDto) {
        groupService.updateGroupManagerPhone(UserUtil.securityUser(authentication), phoneDto);
        return ResponseMessage.builder()
                .message("기업 담당자의 번호 수정을 성공했습니다.")
                .build();
    }


}
