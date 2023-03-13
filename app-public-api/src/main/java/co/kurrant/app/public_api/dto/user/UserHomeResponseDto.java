package co.kurrant.app.public_api.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Schema(description = "대시보드에서 필요한 유저 정보를 가져온다.")
@Getter
@Setter
public class UserHomeResponseDto {
    private BigInteger userId;
    private String name;
    private String phone;
    private String email;
    private Boolean isMembership;
    private BigInteger groupId;
    private String group;
    private Integer spotType;
    private BigInteger spotId;
    private String spot;
    private BigDecimal point;
    private int membershipUsingPeriod;
    private int foundersNumber;
    private int leftFoundersNumber;
}
