package co.dalicious.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class ReviewMappingDto {
    private Integer satisfaction;
    private Integer satisfactionOrigin;
    private String imageOrigin;
    private String content;
    private String contentOrigin;
    private Boolean forMakers;

    public static ReviewMappingDto createDto(String content, String image, Integer satisfaction, Boolean forMakers) {
        return ReviewMappingDto.builder()
                .content(content)
                .contentOrigin(content)
                .imageOrigin(image)
                .satisfaction(satisfaction)
                .satisfactionOrigin(satisfaction)
                .forMakers(forMakers)
                .build();
    }
}
