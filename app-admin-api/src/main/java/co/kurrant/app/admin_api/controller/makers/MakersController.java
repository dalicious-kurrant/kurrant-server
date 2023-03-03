package co.kurrant.app.admin_api.controller.makers;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.LocationTestDto;
import co.kurrant.app.admin_api.dto.makers.SaveMakersRequestDtoList;
import co.kurrant.app.admin_api.service.MakersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    public ResponseMessage saveMakers(@RequestBody SaveMakersRequestDtoList saveMakersRequestDtoList) throws ParseException {
        makersService.saveMakers(saveMakersRequestDtoList);
        return ResponseMessage.builder()
                .message("메이커스 저장에 성공하였습니다.")
                .build();
    }


    @PatchMapping("/location/test")
    public ResponseMessage updateLocation(@RequestBody LocationTestDto locationTestDto) throws ParseException {
        makersService.locationTest(locationTestDto);
                return ResponseMessage.builder()
                        .message("성공!")
                        .build();
    }



}
