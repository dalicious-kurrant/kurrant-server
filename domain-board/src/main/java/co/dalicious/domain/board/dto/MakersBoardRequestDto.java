package co.dalicious.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class MakersBoardRequestDto {
    private String title;
    private String content;
    private BigInteger makersId;
    private Integer boardType;
    private Boolean isStatus;
    private Boolean isAlarmTalk;

}
