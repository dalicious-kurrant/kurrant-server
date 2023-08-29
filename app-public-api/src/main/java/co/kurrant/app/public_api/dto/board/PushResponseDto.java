package co.kurrant.app.public_api.dto.board;

import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.system.util.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Schema(description = "유저 알람 응답 DTO")
public class PushResponseDto {
    private String id;
    private String title;
    private String content;
    private BigInteger userId;
    private String type;
    private String dateTime;
    private Boolean isRead;
    private BigInteger reviewId;
    private BigInteger noticeId;

    public PushResponseDto(PushAlarmHash pushAlarmHash) {
        this.id = pushAlarmHash.getId();
        this.title = pushAlarmHash.getTitle();
        this.content = pushAlarmHash.getMessage();
        this.userId = pushAlarmHash.getUserId();
        this.type = pushAlarmHash.getType();
        this.dateTime = DateUtils.localDateTimeToString(pushAlarmHash.getCreatedDateTime());
        this.isRead = pushAlarmHash.getIsRead();
        this.reviewId = pushAlarmHash.getReviewId();
        this.noticeId = pushAlarmHash.getNoticeId();
    }
}
