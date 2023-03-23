package co.dalicious.domain.file.entity.embeddable;

import co.dalicious.domain.file.converter.ImageTypeConverter;
import co.dalicious.domain.file.dto.ImageCreateRequestDto;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.enums.ImageType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ImageWithEnum extends Image{
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

    @Convert(converter = ImageTypeConverter.class)
    @Comment("이미지 타입")
    private ImageType imageType;
}
