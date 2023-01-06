package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "client__corporation_spot")
public class CorporationSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("기업 스팟 PK")
    private BigInteger id;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    @Comment("스팟 이름")
    private String name;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    private Address address;

    @NotNull
    @Convert(converter = DiningTypesConverter.class)
    @Column(name = "dining_types", nullable = false)
    @Comment("식사 타입")
    private List<DiningType> diningTypes;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("생성일")
    private Timestamp createdDateTime;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false, columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6)")
    @Comment("수정일")
    private Timestamp updatedDateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_corporation_id")
    @JsonManagedReference(value = "client__corporation_fk")
    @Comment("기업")
    private Corporation corporation;

    @Builder
    public CorporationSpot(String name, Address address, String serviceDays, List<DiningType> diningTypes, Corporation corporation) {
        this.name = name;
        this.address = address;
        this.diningTypes = diningTypes;
        this.corporation = corporation;
    }
}
