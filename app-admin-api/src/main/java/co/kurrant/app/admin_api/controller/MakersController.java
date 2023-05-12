package co.kurrant.app.admin_api.controller;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.food.dto.LocationTestDto;
import co.dalicious.domain.food.dto.SaveMakersRequestDtoList;
import co.dalicious.domain.food.dto.UpdateMakersReqDto;
import co.kurrant.app.admin_api.service.MakersService;
import io.swagger.v3.oas.annotations.Operation;
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

    @PatchMapping("")
    @Operation(summary = "메이커스 정보 상세 수정", description = "메이커스의 상세정보를 수정합니다.")
    public ResponseMessage updateMakers(@RequestBody UpdateMakersReqDto updateMakersReqDto) throws ParseException {
        makersService.updateMakers(updateMakersReqDto);
        return ResponseMessage.builder()
                .message("메이커스 정보 수정에 성공했습니다.")
                .build();
    }
}
