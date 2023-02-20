package co.kurrant.app.admin_api.controller;


import co.dalicious.client.core.dto.response.ResponseMessage;
import co.kurrant.app.admin_api.service.ExcelService;
import co.kurrant.app.admin_api.util.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "File")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/admins/files")
@RestController
@CrossOrigin(origins="*", allowedHeaders = "*")
public class FileController {
  private final ExcelService excelService;

  @Operation(summary = "전체 조회 엑셀 파일 불러오기", description = "전체 조회 엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/food/all")
  public ResponseMessage allFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.allFoodExcel(file))
            .build();

  }

  @Operation(summary = "엑셀 파일 불러오기", description = "엑셀 파일의 데이터를 불러옵니다.")
  @PostMapping("/food/makers")
  public ResponseMessage makersFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    return ResponseMessage.builder()
            .message("엑셀 불러오기를 완료했습닌다.")
            .data(excelService.makersFoodExcel(file))
            .build();

  }

  @Operation(summary = "일정 생성", description = "엑셀 파일의 데이터로 예비 식단을 생성합니다..")
  @PostMapping("/schedules")
  public ResponseMessage makersScheduleExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
    UserUtil.securityUser(authentication);
    excelService.createMakersScheduleByExcel(file);
    return ResponseMessage.builder()
            .message("예비 식단 생성을 완료했습닌다.")
            .build();

  }

}

