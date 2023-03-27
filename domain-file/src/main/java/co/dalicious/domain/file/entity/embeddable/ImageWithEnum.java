package co.dalicious.domain.file.entity.embeddable;

import co.dalicious.domain.file.converter.ImageTypeConverter;
import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.dto.ImageWithEnumResponseDto;
import co.dalicious.domain.file.entity.embeddable.enums.ImageType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ImageWithEnum {
    @Column(name = "img_created_datetime", columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @Column(name = "s3_key", length = 1024,
            columnDefinition = "VARCHAR(1024)")
    @Comment("S3 업로드 키")
    private String key;

    @Column(name = "file_location", length = 2048,
            columnDefinition = "VARCHAR(2048)")
    @Comment("S3 접근 위치")
    private String location;

    @Column(name = "filename", length = 1024,
            columnDefinition = "VARCHAR(1024)")
    @Comment("파일명, S3최대값")
    private String filename;
    @NotNull
    @Convert(converter = ImageTypeConverter.class)
    @Comment("이미지 타입")
    private ImageType imageType;

    public ImageWithEnum(ImageResponseDto imageResponseDto, ImageType imageType) {
        this.key = imageResponseDto.getKey();
        this.location = imageResponseDto.getLocation();
        this.filename = imageResponseDto.getFilename();
        this.imageType = imageType;
    }

    public Boolean isSameImage(ImageWithEnumResponseDto imageWithEnumResponseDto) {
        return this.getKey().equals(imageWithEnumResponseDto.getKey()) &&
                this.getLocation().equals(imageWithEnumResponseDto.getLocation()) &&
                this.getFilename().equals(imageWithEnumResponseDto.getFilename()) &&
                this.getImageType().getCode().equals(imageWithEnumResponseDto.getImageType());
    }

    public String getPrefix() {
        String[] str = this.location.split("/");
        return str[3] + "/" + str[4] + "/" + str[5] + "/" + str[6] + "/";
    }

    public static List<String> getImagesLocation(List<Image> images) {
        List<String> locations = new ArrayList<>();
        for (Image image : images) {
            locations.add(image.getLocation());
        }
        return locations;
    }
}
