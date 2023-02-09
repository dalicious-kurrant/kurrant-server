package co.kurrant.app.admin_api.controller;

import co.dalicious.domain.file.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "File")
@RequiredArgsConstructor
@RequestMapping(value = "/v1/files")
@RestController
public class FileController {
  private final ImageService imageService;

//  @Operation(summary = "이미지 업로드 경로 요청", description = "이미지 업로드 경로 요청한다.")
//  @ResponseStatus(HttpStatus.OK)
//  @PostMapping("/request-image-upload-url")
//  public RequestImageUploadUrlResponseDto requestImageUploadUrl(@Parameter(name = "문의 생성 Body",
//      description = "", required = true) @Valid @RequestBody RequestImageUploadUrlRequestDto dto) {
//
//    return imageService.requestUrl(dto);
//  }

}
