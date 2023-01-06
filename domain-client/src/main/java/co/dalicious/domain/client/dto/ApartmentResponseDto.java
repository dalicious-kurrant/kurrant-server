package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "아파트 리스트 응답 DTO")
public class ApartmentResponseDto {
    private BigInteger id;
    private String name;
    private String address;

    @Builder
    public ApartmentResponseDto(BigInteger id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
