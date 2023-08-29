package co.dalicious.data.redis.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class SseReceiverDto {
    private BigInteger receiver;
    private Integer type;
    private String content;
    private BigInteger groupId;
    private BigInteger commentId;

    public SseReceiverDto(BigInteger receiver, Integer type, String content, BigInteger groupId, BigInteger commentId) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.groupId = groupId;
        this.commentId = commentId;
    }
}
