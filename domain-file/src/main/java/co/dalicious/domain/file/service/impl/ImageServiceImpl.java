package co.dalicious.domain.file.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlRequestDto;
import co.dalicious.domain.file.dto.RequestImageUploadUrlResponseDto;
import co.dalicious.domain.file.exception.FileNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import lombok.RequiredArgsConstructor;

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

//  @Value("${cloud.aws.cloudFront.distributionDomain}")
//  private String distributionDomain;
//
//  @Value("${cloud.aws.cloudFront.keyPairId}")
//  private String keyPairId;

  @Override
  @Transactional
  public Image createImage(ImageCreateRequestDto dto) {

    AmazonS3Client client = this.createAmazonS3Client();
    boolean isExist = client.doesObjectExist(this.bucketName, dto.getKey());
    if (!isExist) {
      throw new FileNotFoundException();
    }

    return Image.builder().key(dto.getKey()).location(dto.getLocation()).filename(dto.getFilename())
        .build();
  }

  @Override
  public RequestImageUploadUrlResponseDto requestUrl(RequestImageUploadUrlRequestDto dto) {
    AmazonS3 s3Client = this.createAmazonS3Client();
    String key = this.createKey(dto.getFilename());

    String uploadId = null;
    if (dto.getParts() > 1) {
      InitiateMultipartUploadResult res = s3Client
          .initiateMultipartUpload(new InitiateMultipartUploadRequest(this.bucketName, key));
      uploadId = res.getUploadId();
    }

    List<String> presignedUrls = new ArrayList<String>(dto.getParts());
    for (int i = 0; i < dto.getParts(); i++) {
      Date expiration = DateUtils.addMinutes(new Date(), 15 * i + 540);
      GeneratePresignedUrlRequest generatePresignedUrlRequest =
          new GeneratePresignedUrlRequest(this.bucketName, key);
      generatePresignedUrlRequest.setExpiration(expiration);

      // TODO: 멀티파트 업로드 사용법은 추후 작업
      generatePresignedUrlRequest.withMethod(HttpMethod.PUT);

      if (dto.getParts() > 1 && uploadId != null) {
        generatePresignedUrlRequest.addRequestParameter("uploadId", uploadId);
        generatePresignedUrlRequest.addRequestParameter("partNumber", String.valueOf(i + 1));
      }

      URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
      presignedUrls.add(url.toString());
    }

    String location = this.extractLocation(presignedUrls.get(0));

    return RequestImageUploadUrlResponseDto.builder().key(key).location(location)
        .urls(presignedUrls).uploadId(uploadId).build();
  }

  private String createKey(String filename) {
    StringJoiner sj = new StringJoiner("/");

    long ms = System.currentTimeMillis();
    sj.add(StringUtils.leftPad(String.valueOf(ms), 16, '0'));
    sj.add(filename);

    return sj.toString();
  }

  private String extractLocation(String location) {
    return location.replace(bucketName, "")
        .replaceAll("\\?.*", "");
  }

  public AmazonS3Client createAmazonS3Client() {
    BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
    return (AmazonS3Client) AmazonS3ClientBuilder.standard().withRegion(region)
        .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
  }

}
