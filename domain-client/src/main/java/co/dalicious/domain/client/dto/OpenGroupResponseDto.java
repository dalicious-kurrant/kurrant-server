package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@Schema(description = "오픈 그룹 리스트 응답 DTO")
public class OpenGroupResponseDto {
    private BigInteger id;
    private String name;
    private String address;
    private String jibunAddress;
    private String latitude;
    private String longitude;
    private List<Integer> diningType;
    private Integer spotType;
    private Integer userCount;
}
