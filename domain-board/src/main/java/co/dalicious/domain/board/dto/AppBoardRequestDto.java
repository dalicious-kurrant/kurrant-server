package co.dalicious.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class AppBoardRequestDto {
    private String title;
    private String content;
    private List<BigInteger> groupIds;
    private Integer boardType;
    private Boolean isStatus;
    private Boolean isPushAlarm;
}
