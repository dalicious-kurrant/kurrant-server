package co.dalicious.domain.food.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DailyFoodCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    
}
