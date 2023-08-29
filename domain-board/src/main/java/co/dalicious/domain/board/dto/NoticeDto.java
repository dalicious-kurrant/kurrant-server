package co.dalicious.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class NoticeDto {
    private BigInteger id;
    private String created;
    private String updated;
    private String title;
    private String content;
    private Boolean status;
    private Integer boardType;
    private List<Integer> boardOption;
}
