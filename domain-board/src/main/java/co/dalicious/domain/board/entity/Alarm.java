package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.AlarmTypeConverter;
import co.dalicious.domain.board.entity.enums.AlarmBoardType;
import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board__alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Column(name ="title", columnDefinition = "VARCHAR(45)")
    private String title;

    @Column(name ="content", columnDefinition = "VARCHAR(255)")
    private String content;

    @Convert(converter = AlarmTypeConverter.class)
    @Column(name ="alarm_type", columnDefinition = "VARCHAR(45)")
    private AlarmBoardType type;

    @Column(name="created_date", columnDefinition = "DATETIME")
    private LocalDateTime created;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id",nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;

    @Builder
    public Alarm(BigInteger id, String title, String content, String type, LocalDateTime created, BigInteger user){
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = AlarmBoardType.valueOf(type);
        this.created = created;
        this.user = User.builder().id(user).build();
    }
}
