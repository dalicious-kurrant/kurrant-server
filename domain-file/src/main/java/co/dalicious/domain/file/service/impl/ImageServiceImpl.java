package co.dalicious.domain.file.service.impl;


import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.service.ImageService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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
    public List<ImageResponseDto> upload(List<MultipartFile> multipartFiles, String dirName) throws IOException {
        List<ImageResponseDto> imageResponseDtos = new ArrayList<>();

        AmazonS3 amazonS3 = amazonS3Client();
        if (multipartFiles.isEmpty()) {
            throw new ApiException(ExceptionEnum.FILE_NOT_FOUND);
        }

        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            String key = dirName + "/" + createKey(fileName);
            amazonS3.putObject(new PutObjectRequest(bucketName, key, multipartFile.getInputStream(), null));
            String location = String.valueOf(amazonS3.getUrl(bucketName, key));
            ImageResponseDto imageResponseDto = new ImageResponseDto(location, key, fileName);
            imageResponseDtos.add(imageResponseDto);
        }
        return imageResponseDtos;
    }

    @Override
    public ImageResponseDto upload(MultipartFile multipartFile, String dirName) throws IOException {
        AmazonS3 amazonS3 = amazonS3Client();
        if (multipartFile.isEmpty()) {
            throw new ApiException(ExceptionEnum.FILE_NOT_FOUND);
        }

        String fileName = multipartFile.getOriginalFilename();
        String key = dirName + "/" + createKey(fileName);
        amazonS3.putObject(new PutObjectRequest(bucketName, key, multipartFile.getInputStream(), null));
        String location = String.valueOf(amazonS3.getUrl(bucketName, key));
        return new ImageResponseDto(location, key, fileName);
    }

    public void delete(String prefix) {
        AmazonS3 amazonS3 = amazonS3Client();
        ObjectListing objectListing = amazonS3.listObjects(bucketName, prefix);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        for (S3ObjectSummary objectSummary : objectSummaries) {
            amazonS3.deleteObject(bucketName, objectSummary.getKey());
        }
    }

    public byte[] downloadImageFromS3(String key) {
        AmazonS3 amazonS3 = amazonS3Client();

        S3Object s3Object = amazonS3.getObject(bucketName, key);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }
}
