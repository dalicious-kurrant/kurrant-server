package co.kurrant.app.public_api.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "비밀번호 변경 요청 DTO")
@Getter
@NoArgsConstructor
public class CustomerServiceDto {
    private String title;
    private String question;
    private String answer;
    private LocalDate created;
    private LocalDate updated;
    private Integer type;
    private Integer title_no;

}
