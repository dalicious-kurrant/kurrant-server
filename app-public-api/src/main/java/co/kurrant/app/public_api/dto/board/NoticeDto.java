package co.kurrant.app.public_api.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Schema(description = "공지사항 응답 DTO")
@Getter
@Setter
public class NoticeDto {
    private BigInteger id;
    private String created;
    private String updated;
    private String title;
    private String content;
    private Integer type;
}
