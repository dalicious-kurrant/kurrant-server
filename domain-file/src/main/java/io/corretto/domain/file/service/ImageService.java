package io.corretto.domain.file.service;

import io.corretto.domain.file.dto.ImageCreateRequestDto;
import io.corretto.domain.file.dto.RequestImageUploadUrlRequestDto;
import io.corretto.domain.file.dto.RequestImageUploadUrlResponseDto;
import io.corretto.domain.file.entity.embeddable.Image;

public interface ImageService {
  public Image createImage(ImageCreateRequestDto imageRequestDto);

  public RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto);

}
