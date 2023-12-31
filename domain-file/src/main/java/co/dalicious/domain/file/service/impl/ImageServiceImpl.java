package co.dalicious.domain.file.service.impl;


import java.io.IOException;
import java.util.StringJoiner;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import exception.ApiException;
import exception.ExceptionEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;
  @Value("${cloud.aws.credentials.accessKey}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secretKey}")
  private String secretKey;

  @Value("${cloud.aws.region.static}")
  private String region;

  @PostConstruct
  public AmazonS3 amazonS3Client() {
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    return AmazonS3ClientBuilder.standard()
            .withRegion(region)
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .build();
  }

  // 버킷에 파일을 올린 후, 지정해줄 이름(고유값)
  private String createKey(String filename) {
    StringJoiner sj = new StringJoiner("/");

    long ms = System.currentTimeMillis();
    sj.add(StringUtils.leftPad(String.valueOf(ms), 16, '0'));
    sj.add(filename);

    return sj.toString();
  }

  // Key가 URL encoded된 값. 프론트엔드에서 바로 참조하는 값.
  private String extractLocation(String location) {
    return location.replace(bucketName, "")
        .replaceAll("\\?.*", "");
  }

  @Override
  public Image upload(MultipartFile multipartFile, String dirName) throws IOException {
    AmazonS3 amazonS3 = amazonS3Client();
    if(multipartFile.isEmpty()) {
      throw new ApiException(ExceptionEnum.FILE_NOT_FOUND);
    }
    String fileName = multipartFile.getOriginalFilename();
    String key = dirName + "/" + createKey(fileName);
    amazonS3.putObject(new PutObjectRequest(bucketName, key, multipartFile.getInputStream(), null));
    String location = String.valueOf(amazonS3.getUrl(bucketName, key));
    return Image.builder()
            .key(key)
            .location(location)
            .filename(fileName)
            .build();
  }

  @Override
  public void delete(String key) {
    AmazonS3 amazonS3 = amazonS3Client();
    amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
  }

  //  @Override
//  @Transactional
//  public Image createImage(ImageCreateRequestDto dto) {
//
//    AmazonS3Client client = this.createAmazonS3Client();
//    boolean isExist = client.doesObjectExist(this.bucketName, dto.getKey());
//    if (!isExist) {
//      throw new FileNotFoundException();
//    }
//
//    return Image.builder().key(dto.getKey()).location(dto.getLocation()).filename(dto.getFilename())
//        .build();
//  }
//
//  @Override
//  public RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto) {
//    AmazonS3 s3Client = this.createAmazonS3Client();
//    String key = this.createKey(dto.getFilename());
//
//    String uploadId = null;
//    if (dto.getParts() > 1) {
//      InitiateMultipartUploadResult res = s3Client
//          .initiateMultipartUpload(new InitiateMultipartUploadRequest(this.bucketName, key));
//      uploadId = res.getUploadId();
//    }
//
//    List<String> presignedUrls = new ArrayList<String>(dto.getParts());
//    for (int i = 0; i < dto.getParts(); i++) {
//      Date expiration = DateUtils.addMinutes(new Date(), 15 * i + 540);
//      GeneratePresignedUrlRequest generatePresignedUrlRequest =
//          new GeneratePresignedUrlRequest(this.bucketName, key);
//      generatePresignedUrlRequest.setExpiration(expiration);
//
//      // TODO: 멀티파트 업로드 사용법은 추후 작업
//      generatePresignedUrlRequest.withMethod(HttpMethod.PUT);
//
//      if (dto.getParts() > 1 && uploadId != null) {
//        generatePresignedUrlRequest.addRequestParameter("uploadId", uploadId);
//        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(i + 1));
//      }
//
//      URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
//      presignedUrls.add(url.toString());
//    }
//
//    String location = this.extractLocation(presignedUrls.get(0));
//
//    return RequestImageUploadUrlResponseDto.builder().key(key).location(location)
//        .urls(presignedUrls).uploadId(uploadId).build();
//  }

}
