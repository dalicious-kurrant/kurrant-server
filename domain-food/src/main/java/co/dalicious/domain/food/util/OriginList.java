package co.dalicious.domain.food.util;

import co.dalicious.domain.food.entity.Origin;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "원산지 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OriginList {
    private String name;
    private String from;

    @Builder
    OriginList(Origin origin){
        this.name = origin.getName();
        this.from = origin.getFrom();
    }
}
