package co.dalicious.domain.banner.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import co.dalicious.domain.banner.converter.BannerSectionConverter;
import co.dalicious.domain.banner.converter.BannerTypeConverter;
import co.dalicious.domain.banner.enums.BannerSection;
import co.dalicious.domain.banner.enums.BannerType;
import org.hibernate.annotations.*;
import co.dalicious.domain.file.entity.embeddable.Image;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cms__banner")
public class Banner {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BIGINT UNSIGNED COMMENT '배너 PK'")
    private BigInteger id;

    @Comment("배너 게시 시작 날짜")
    private LocalDate startDate;
    @Comment("배너 게시 종료 날짜")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
    private Timestamp updatedDateTime;

    @Convert(converter = BannerTypeConverter.class)
    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '배너유형'")
    private BannerType type;

    @Convert(converter = BannerSectionConverter.class)
    @Column(name = "section", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '배너 구역'")
    private BannerSection section;

    @Embedded
    private Image image;

    @Builder
    public Banner(LocalDate startDate, LocalDate endDate, BannerType type, BannerSection section, Image image) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.section = section;
        this.image = image;
    }
}
