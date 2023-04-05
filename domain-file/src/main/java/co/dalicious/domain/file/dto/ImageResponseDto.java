package co.dalicious.domain.file.dto;

import co.dalicious.domain.file.entity.embeddable.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "이미지 응답형태")
@Builder
@Getter
public class ImageResponseDto {
  @Schema(description = "S3 이미지 location", required = true)
  private String location;

  @Schema(description = "S3 이미지 key", required = true)
  private String key;

  @Schema(description = "파일명", required = true)
  private String filename;

  public ImageResponseDto(String location, String key, String filename) {
    this.location = location;
    this.key = key;
    this.filename = filename;
  }

  public ImageResponseDto(Image image) {
    this.location = image.getLocation();
    this.key = image.getKey();
    this.filename = image.getFilename();
  }
}
