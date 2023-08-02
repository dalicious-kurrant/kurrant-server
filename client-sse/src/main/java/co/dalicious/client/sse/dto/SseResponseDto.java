package co.dalicious.client.sse.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class SseResponseDto {
    String id;
    Integer type;
    Boolean isRead;
    String content;
    String createDate;
    BigInteger groupId;
    BigInteger commentId;

}
