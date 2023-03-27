package co.dalicious.domain.file.entity.embeddable.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ImageType {
    BUSINESS_LICENSE("사업자등록증", 1),
    BUSINESS_PERMIT("영업신고증", 2),
    ACCOUNT_COPY("통장사본", 3);

    private final String imageType;
    private final Integer code;

    ImageType(String imageType, Integer code) {
        this.imageType = imageType;
        this.code = code;
    }

    public static ImageType ofCode(Integer dbData) {
        return Arrays.stream(ImageType.values())
                .filter(v -> v.getCode().equals(dbData))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
