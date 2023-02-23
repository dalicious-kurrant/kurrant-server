package co.dalicious.domain.file.entity.embeddable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.ImageResponseDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Image {
    @Column(name = "img_created_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
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

    @Builder
    public Image(String key, String location, String filename) {
        this.key = key;
        this.location = location;
        this.filename = filename;
    }

    public Image(ImageCreateRequestDto imageCreateRequestDto) {
        this.key = imageCreateRequestDto.getKey();
        this.location = imageCreateRequestDto.getLocation();
        this.filename = imageCreateRequestDto.getFilename();
    }

    public Image(ImageResponseDto imageResponseDto) {
        this.key = imageResponseDto.getKey();
        this.location = imageResponseDto.getLocation();
        this.filename = imageResponseDto.getFilename();
    }

    public static List<Image> toImages(List<ImageResponseDto> imageResponseDtos) {
        List<Image> images = new ArrayList<>();
        for (ImageResponseDto imageResponseDto : imageResponseDtos) {
            images.add(new Image(imageResponseDto));
        }
        return images;
    }

    public String getPrefix() {
        String[] str = this.location.split("/");
        return str[0] + "/" + str[1] + "/";
    }

    public static List<String> getImagesLocation(List<Image> images) {
        List<String> locations = new ArrayList<>();
        for (Image image : images) {
            locations.add(image.getLocation());
        }
        return locations;
    }
}
