package co.kurrant.app.public_api.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Schema(description = "알림센터 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
public class AlarmDto {
    private BigInteger id;
    private String content;
    private String created;
    private String title;
    private Integer type;
    private BigInteger userId;
}
