package co.dalicious.domain.board.entity;

import co.dalicious.domain.board.converter.AlarmTypeConverter;
import co.dalicious.domain.board.entity.enums.AlarmType;
import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDate;

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
    private AlarmType type;

    @CreatedDate
    @Column(name="created_date", columnDefinition = "DATE")
    private LocalDate created;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id",nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private User user;
}
