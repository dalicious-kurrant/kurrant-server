package co.dalicious.domain.user.entity;


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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__admin_user_history")
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("유저 히스토리 PK")
    private BigInteger id;

    @Comment(value = "삭제한 유저 아이디")
    private BigInteger userId;

    @Comment(value = "이름")
    private String name;

    @Comment(value = "이메일")
    private String email;

    @Comment(value = "휴대폰번호")
    private String phone;

    @Comment(value = "삭제한 유저의 그룹 아이디")
    private BigInteger groupId;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_datetime", nullable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("삭제한 일시")
    private Timestamp createdDateTime;

    @Builder
    UserHistory(BigInteger userId, String name, String email, String phone, BigInteger groupId){
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.groupId = groupId;
    }

}
