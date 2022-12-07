package co.dalicious.domain.file.service;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {
  Image upload(MultipartFile multipartFile, String dirName) throws IOException;
  void delete(String key, String dirName);
//  Image createImage(ImageCreateRequestDto imageRequestDto);
//
//  RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto);

}
