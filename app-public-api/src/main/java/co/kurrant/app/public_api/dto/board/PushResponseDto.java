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
    private String message;
    private BigInteger userId;
    private String redirectUrl;
    private String createdDateTime;
    private Boolean isRead;
    private BigInteger reviewId;

    public PushResponseDto(PushAlarmHash pushAlarmHash) {
        this.id = pushAlarmHash.getId();
        this.title = pushAlarmHash.getTitle();
        this.message = pushAlarmHash.getMessage();
        this.userId = pushAlarmHash.getUserId();
        this.redirectUrl = pushAlarmHash.getRedirectUrl();
        this.createdDateTime = DateUtils.localDateTimeToString(pushAlarmHash.getCreatedDateTime());
        this.isRead = pushAlarmHash.getIsRead();
        this.reviewId = pushAlarmHash.getReviewId();
    }
}
