package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigInteger;

@Schema(description = "원산지 정보")
@Getter
@NoArgsConstructor
public class OriginDto {
    private String name;
    private String from;

    @Builder
    public OriginDto(String name, String from) {
        this.name = name;
        this.from = from;
    }

    @Getter
    @Setter
    public static class WithId {
        private BigInteger id;
        private String name;
        private String from;
    }
}
