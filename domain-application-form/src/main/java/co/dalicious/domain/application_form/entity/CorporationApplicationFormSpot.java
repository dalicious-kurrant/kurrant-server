package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.enums.DiningType;
import co.dalicious.system.util.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "application_form__spot")
public class CorporationApplicationFormSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    @Comment("컬럼명")
    private String name;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    @Comment("주소")
    private Address address;

    @NotNull
    @Convert(converter = DiningTypesConverter.class)
    @Column(name = "dining_types", nullable = false)
    @Comment("스팟별 식사 일정(아침, 점심, 저녁)")
    private List<DiningType> diningTypes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_form__corporation_id")
    @JsonBackReference(value = "application_form__corporation_fk")
    private CorporationApplicationForm corporationApplicationForm;

    @Builder
    public CorporationApplicationFormSpot(String name, Address address, List<DiningType> diningTypes) {
        this.name = name;
        this.address = address;
        this.diningTypes = diningTypes;
    }

    public void setCorporationApplicationForm(CorporationApplicationForm corporationApplicationForm) {
        this.corporationApplicationForm = corporationApplicationForm;
    }


}