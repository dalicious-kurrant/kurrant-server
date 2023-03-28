package co.kurrant.app.admin_api.controller.client;

import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.client.dto.UpdateSpotDetailRequestDto;
import co.kurrant.app.admin_api.dto.client.SaveSpotList;
import co.kurrant.app.admin_api.service.SpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@Tag(name = "3.Client")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/clients")
@RestController
public class SpotController {

    private final SpotService spotService;

    @Operation(summary = "그룹 리스트 조회", description = "존재하는 그룹의 리스트를 조회한다.")
    @GetMapping("/spots")
    public ResponseMessage getSpotList() {
        return ResponseMessage.builder()
                .message("그룹 리스트 조회에 성공하였습니다.")
                .data(spotService.getGroupList())
                .build();
    }

    @Operation(summary = "스팟정보 전체 조회", description = "존재하는 스팟을 모두 조회합니다.")
    @GetMapping("/spot/all")
    public ResponseMessage getAllSpotList(@RequestParam(required = false) Integer status) {
        return ResponseMessage.builder()
                .message("모든 스팟을 조회했습니다.")
                .data(spotService.getAllSpotList(status))
                .build();
    }


    @Operation(summary = "저장하기", description = "수정사항을 저장한다.")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public ResponseMessage saveSpotList(@RequestBody SaveSpotList saveSpotList) throws ParseException {
        spotService.saveSpotList(saveSpotList);
        return ResponseMessage.builder()
                .message("저장에 성공하였습니다.")
                .build();
    }

    @Operation(summary = "스팟 삭제하기", description = "선택한 스팟을 삭제한다.")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("")
    public ResponseMessage deleteSpot(@RequestBody List<BigInteger> spotIdList){
        spotService.deleteSpot(spotIdList);
        return ResponseMessage.builder()
                .message("선택한 스팟를 비활성 처리했습니다.")
                .build();
    }


    @Operation(summary = "스팟정보 상세 조회", description = "스팟의 상세정보를 조회합니다.")
    @GetMapping("/spot/detail")
    public ResponseMessage getSpotDetail(@RequestParam(required = true) Integer spotId) {
        return ResponseMessage.builder()
                .message("스팟 상세 정보를 조회했습니다.")
                .data(spotService.getSpotDetail(spotId))
                .build();
    }

    @Operation(summary = "스팟정보 상세 수정", description = "스팟의 상세정보를 수정합니다.")
    @PatchMapping("/spot/detail")
    public ResponseMessage updateSpotDetail(@RequestBody UpdateSpotDetailRequestDto updateSpotDetailRequestDto) throws ParseException {
        spotService.updateSpotDetail(updateSpotDetailRequestDto);
        return ResponseMessage.builder()
                .message("스팟 상세 정보를 수정했습니다.")
                .build();
    }



}
