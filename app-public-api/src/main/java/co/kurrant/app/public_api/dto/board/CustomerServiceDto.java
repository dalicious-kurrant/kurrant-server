package co.kurrant.app.public_api.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Schema(description = "비밀번호 변경 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
public class CustomerServiceDto {
    private String title;
    private String question;
    private String answer;
    private String created;
    private String updated;
    private Integer type;
    private Integer titleNo;

}
