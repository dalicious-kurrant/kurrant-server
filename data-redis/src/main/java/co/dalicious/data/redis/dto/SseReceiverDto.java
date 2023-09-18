package co.dalicious.data.redis.dto;

import lombok.Builder;
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
    private BigInteger noticeId;

    @Builder
    public SseReceiverDto(BigInteger receiver, Integer type, String content, BigInteger groupId, BigInteger commentId, BigInteger noticeId) {
        this.receiver = receiver;
        this.type = type;
        this.content = content;
        this.groupId = groupId;
        this.commentId = commentId;
        this.noticeId = noticeId;
    }
}
