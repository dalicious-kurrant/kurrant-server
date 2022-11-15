package io.corretto.domain.file.dto;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "이미지 응답 쿼리")
@Getter
@Builder
public class RequestImageUploadUrlResponseDto {
  @Schema(description = "업로드 ID")
  private String uploadId;

  @Schema(description = "업로드 키")
  private String key;

  @Schema(description = "업로드 로케이션")
  private String location;

  @Schema(description = "업로드 URL, parts갯수마다 목록이 달라진다.")
  private List<String> urls;

}
