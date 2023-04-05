package co.dalicious.client.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user__refresh_token", indexes = @Index(name = "i_user_id", columnList = "userId"))
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("유저 PK")
    private BigInteger userId;

    @Comment("리프레시 토큰")
    private String refreshToken;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @Builder
    public RefreshToken(BigInteger userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }
}
