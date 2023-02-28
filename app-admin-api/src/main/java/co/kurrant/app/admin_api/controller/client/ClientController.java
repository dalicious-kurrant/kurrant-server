package co.kurrant.app.admin_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.dto.client.DeleteSpotRequestDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3.Client")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/clients")
@RestController
public class ClientController {

    private final SpotService spotService;

    @Operation(summary = "스팟정보 전체 조회", description = "존재하는 스팟을 모두 조회합니다.")
    @GetMapping("/spot/all")
    public ResponseMessage getAllSpotList() {
        return ResponseMessage.builder()
                .message("모든 스팟을 조회했습니다.")
                .data(spotService.getAllSpotList())
                .build();
    }


    @Operation(summary = "저장하기", description = "수정사항을 저장한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseMessage saveSpotList(@RequestBody SaveSpotList saveSpotList){
        spotService.saveSpotList(saveSpotList);
        return ResponseMessage.builder()
                .message("저장에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스팟 삭제하기", description = "선택한 스팟을 삭제한다.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("")
    public ResponseMessage deleteSpot(@RequestBody DeleteSpotRequestDto deleteSpotRequestDto){
        spotService.deleteSpot(deleteSpotRequestDto);
        return ResponseMessage.builder()
                .message("선택한 스팟를 비활성 처리했습니다.")
                .build();
    }




}
