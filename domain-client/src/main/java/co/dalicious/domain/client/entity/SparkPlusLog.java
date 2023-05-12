package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.converter.SparkPlusLogTypeConverter;
import co.dalicious.domain.client.entity.enums.SparkPlusLogType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "etc__spark_plus_log", indexes = @Index(name = "i_spark_plus_log_type", columnList = "spark_plus_log_type"))
public class SparkPlusLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("스파크플러스 로그 ID")
    private Integer id;

    @Convert(converter = SparkPlusLogTypeConverter.class)
    @Comment("로그 타입")
    @Column(unique = true, name = "spark_plus_log_type")
    private SparkPlusLogType sparkPlusLogType;

    @Comment("버튼 클릭 횟수")
    private Long count;

    public void addCount() {
        this.count = this.count + 1;
    }
}
