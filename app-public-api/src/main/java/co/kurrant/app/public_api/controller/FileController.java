package co.kurrant.app.public_api.controller;

import javax.validation.Valid;

import co.dalicious.domain.file.entity.embeddable.Image;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import co.dalicious.domain.file.dto.RequestImageUploadUrlRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlResponseDto;
import co.dalicious.domain.file.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

  @Operation(summary = "이미지 업로드 경로 요청", description = "이미지 업로드 경로 요청한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/upload")
  public Image uploadImage(MultipartFile multipartFile) throws IOException {

    return imageService.upload(multipartFile, "test");
  }

  @Operation(summary = "이미지 삭제 요청", description = "이미지 업로드 경로 요청한다.")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/delete")
  public void requestImageUploadUrl() {
    String dirName = "test";
    String key = "0001670392620211";
    String fileName = "images.jpeg";
    imageService.delete(dirName + "/" + key + "/" + fileName);
  }
}
