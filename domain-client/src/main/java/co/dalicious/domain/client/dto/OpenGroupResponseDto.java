package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "아파트 리스트 응답 DTO")
public class OpenGroupResponseDto {
    private BigInteger id;
    private String name;
    private String address;
    private Integer spotType;

    @Builder
    public OpenGroupResponseDto(BigInteger id, String name, String address, Integer spotType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.spotType = spotType;
    }
}
