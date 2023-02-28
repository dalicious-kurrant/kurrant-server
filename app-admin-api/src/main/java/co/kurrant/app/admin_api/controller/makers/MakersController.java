package co.kurrant.app.admin_api.controller.makers;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.model.SecurityUser;
import co.kurrant.app.admin_api.service.MakersService;
import co.kurrant.app.admin_api.util.UserUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3. Makers")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers")
@RestController
public class MakersController {

    private final MakersService makersService;

    @GetMapping("")
    public ResponseMessage findAllMakersInfo(){
        return ResponseMessage.builder()
                .data(makersService.findAllMakersInfo())
                .message("메이커스 조회에 성공하였습니다.")
                .build();
    }





}
