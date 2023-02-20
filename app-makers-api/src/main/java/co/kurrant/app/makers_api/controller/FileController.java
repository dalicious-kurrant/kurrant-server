package co.kurrant.app.makers_api.controller;


import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.makers_api.service.ExcelService;
import co.kurrant.app.makers_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "File")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/files")
@RestController
public class FileController {
  private final ExcelService excelService;

  @Operation(summary = "전체 조회 엑셀 파일 불러오기", description = "전체 조회 엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/excel/all")
  public ResponseMessage allFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.allFoodExcel(file))
            .build();

  }

  @Operation(summary = "엑셀 파일 불러오기", description = "엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/excel")
  public ResponseMessage makersFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.makersFoodExcel(file))
            .build();

  }

}

