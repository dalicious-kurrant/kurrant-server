package co.kurrant.app.makers_api.controller;


import co.dalicious.client.core.dto.response.ResponseMessage;
import co.dalicious.domain.file.service.ImageService;
import co.kurrant.app.makers_api.service.MakersExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "File")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/makers/files")
@RestController
public class FileController {
  private final MakersExcelService makersExcelService;
  private final ImageService imageService;

  @Operation(summary = "이미지 업로드 경로 요청", description = "이미지 업로드 경로 요청한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/images")
  public ResponseMessage uploadImage(List<MultipartFile> multipartFiles) throws IOException {
    return ResponseMessage.builder()
            .data(imageService.upload(multipartFiles, "food"))
            .message("S3 이미지 업로드에 성공하였습니다.")
            .build();
  }

//  @Operation(summary = "전체 조회 엑셀 파일 불러오기", description = "전체 조회 엑셀 파일의 데이터를 불러옵니다.")
//  @PostMapping("/excel/all")
//  public ResponseMessage allFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
//    UserUtil.securityUser(authentication);
//    return ResponseMessage.builder()
//            .message("엑셀 불러오기를 완료했습닌다.")
//            .data(makersExcelService.allFoodExcel(file))
//            .build();
//
//  }
//
//  @Operation(summary = "엑셀 파일 불러오기", description = "엑셀 파일의 데이터를 불러옵니다.")
//  @PostMapping("/excel")
//  public ResponseMessage makersFoodExcel(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
//    UserUtil.securityUser(authentication);
//    return ResponseMessage.builder()
//            .message("엑셀 불러오기를 완료했습닌다.")
//            .data(makersExcelService.makersFoodExcel(file))
//            .build();
//
//  }

}

