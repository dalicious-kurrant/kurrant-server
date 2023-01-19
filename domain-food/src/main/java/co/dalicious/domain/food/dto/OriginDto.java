package co.dalicious.domain.food.dto;

import co.dalicious.domain.food.entity.Origin;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "원산지 정보")
@Getter
@Setter
public class OriginDto {
    private String name;
    private String from;

    @Builder
    public OriginDto(String name, String from) {
        this.name = name;
        this.from = from;
    }
}
