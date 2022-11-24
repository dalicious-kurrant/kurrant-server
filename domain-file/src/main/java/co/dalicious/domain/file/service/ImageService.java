package co.dalicious.domain.file.service;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;

public interface ImageService {
  public Image createImage(ImageCreateRequestDto imageRequestDto);

  public RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto);

}
