package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "아파트 리스트 응답 DTO")
public class OpenGroupResponseDto {
    private BigInteger id;
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private List<Integer> diningType;
    private Integer spotType;
    private Integer userCount;

    @Builder
    public OpenGroupResponseDto(BigInteger id, String name, String address, List<Integer> diningType, Integer spotType, Integer userCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.diningType = diningType;
        this.spotType = spotType;
        this.userCount = userCount;
    }
}
