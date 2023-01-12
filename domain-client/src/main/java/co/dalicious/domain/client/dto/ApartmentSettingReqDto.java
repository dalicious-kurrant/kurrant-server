package co.dalicious.domain.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "유저 아파트 그룹 설정 요청 DTO")
public class ApartmentSettingReqDto {
    BigInteger id;
}
