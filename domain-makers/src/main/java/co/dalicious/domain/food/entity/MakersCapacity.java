package co.dalicious.domain.food.entity;

import co.dalicious.system.converter.DiningTypeConverter;
import co.dalicious.system.enums.DiningType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "makers__makers_capacity")
public class MakersCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("ID")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "makers_fk")
    private Makers makers;

    @Convert(converter = DiningTypeConverter.class)
    @Comment("식사타입")
    private DiningType diningType;

    @Comment("식사 일정별 가능 수량")
    private Integer capacity;
}
