package co.dalicious.domain.user.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor
@Getter
@Table(name = "client__apartment")
public class Apartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("아파트 고객사 PK")
    private BigInteger id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "family_count")
    private Integer familyCount;

    @Column(name = "manager_id")
    private Long managerId;

    @Embedded
    private Address address;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    private DiningType diningType;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Size(max = 32)
    @NotNull
    @Column(name = "delivery_time", nullable = false, length = 32)
    private String deliveryTime;

    @Size(max = 255)
    @Column(name = "emb_use_days")
    private String embUseDays;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "apartment-fk")
    private List<User> users;


    @Builder
    public Apartment(String name, Integer familyCount, DiningType diningType, String deliveryTime) {
        this.name = name;
        this.familyCount = familyCount;
        this.diningType = diningType;
        this.deliveryTime = deliveryTime;
    }
}