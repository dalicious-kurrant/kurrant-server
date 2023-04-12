package co.dalicious.domain.file.service;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ImageService {
    List<ImageResponseDto> upload(List<MultipartFile> multipartFiles, String dirName) throws IOException;
    ImageResponseDto upload(MultipartFile multipartFile, String dirName) throws IOException;
    void delete(String key);
    byte[] downloadImageFromS3(String key);
//  Image createImage(ImageCreateRequestDto imageRequestDto);
//
//  RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto);

}
