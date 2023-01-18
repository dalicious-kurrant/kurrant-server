package co.dalicious.domain.order.entity;

import co.dalicious.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
@Getter
@Table(name = "order__cart")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("TYPE")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn
    @Comment("유저 Id")
    private User user;

    @Column(name = "count")
    @Comment("수량")
    private Integer count;

    @CreationTimestamp
    @Column(name = "created_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private LocalDate created;

    @UpdateTimestamp
    @Column(name = "updated_datetime",
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private LocalDate updated;

    public void updateCount(Integer count) {
        this.count = count;
    }
}