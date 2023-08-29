package co.dalicious.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
public class AppBoardResponseDto {
    private BigInteger id;
    private String title;
    private String content;
    private List<String> groupNames;
    private Integer boardType;
    private Boolean isStatus;
    private String createDate;
    private Boolean isPushAlarm;
    private List<Integer> boardOption;
}
