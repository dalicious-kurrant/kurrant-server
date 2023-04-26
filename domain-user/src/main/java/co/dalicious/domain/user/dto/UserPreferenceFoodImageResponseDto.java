package co.dalicious.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "회원정보 입력 food image 응답 Dto")
public class UserPreferenceFoodImageResponseDto {

    private BigInteger foodId;
    private String imageUrl;

}
