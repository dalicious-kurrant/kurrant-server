package co.dalicious.domain.food.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "Location 테스트용 DTO")
public class LocationTestDto {
    private String location;
    private BigInteger id;
}
