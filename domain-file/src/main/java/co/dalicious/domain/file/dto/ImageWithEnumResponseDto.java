package co.dalicious.domain.file.dto;

import co.dalicious.domain.file.entity.embeddable.ImageWithEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "이미지 응답형태")
@Builder
@Getter
@NoArgsConstructor
public class ImageWithEnumResponseDto {
  @Schema(description = "S3 이미지 타입", required = true)
  private Integer imageType;
  @Schema(description = "S3 이미지 location", required = true)
  private String location;

  @Schema(description = "S3 이미지 key", required = true)
  private String key;

  @Schema(description = "파일명", required = true)
  private String filename;

  public ImageWithEnumResponseDto(Integer imageType, String location, String key, String filename) {
    this.imageType = imageType;
    this.location = location;
    this.key = key;
    this.filename = filename;
  }

  public ImageWithEnumResponseDto(ImageWithEnum imageWithEnum) {
    this.imageType = imageWithEnum.getImageType().getCode();
    this.location = imageWithEnum.getLocation();
    this.key = imageWithEnum.getKey();
    this.filename = imageWithEnum.getFilename();
  }
}
