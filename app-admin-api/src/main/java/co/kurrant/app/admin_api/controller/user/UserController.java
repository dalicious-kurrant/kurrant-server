package co.kurrant.app.admin_api.controller.user;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.user.dto.DeleteMemberRequestDto;
import co.kurrant.app.admin_api.dto.user.SaveAndUpdateUserList;
import co.kurrant.app.admin_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4.User")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저조회", description = "유저 목록을 조회한다.")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all")
    public ResponseMessage getUserList(@PageableDefault(size = 20, sort = "id",
            direction = Sort.Direction.ASC) OffsetBasedPageRequest pageable) {
        return ResponseMessage.builder()
                .data(userService.getUserList(pageable))
                .message("유저 목록 조회")
                .build();
    }

    @Operation(summary = "선택 유저 탈퇴처리", description = "선택한 유저를 탈퇴처리한다")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("")
    public ResponseMessage deleteMember(@RequestBody DeleteMemberRequestDto deleteMemberRequestDto){
        userService.deleteMember(deleteMemberRequestDto);
        return ResponseMessage.builder()
                .message("선택한 유저를 탈퇴처리했습니다.")
                .build();
    }

    @Operation(summary = "저장하기", description = "수정사항을 저장한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseMessage saveUserList(@RequestBody SaveAndUpdateUserList saveAndUpdateUserList ){
        userService.saveUserList(saveAndUpdateUserList);
        return ResponseMessage.builder()
                .message("저장에 성공하였습니다.")
                .build();
    }

}
