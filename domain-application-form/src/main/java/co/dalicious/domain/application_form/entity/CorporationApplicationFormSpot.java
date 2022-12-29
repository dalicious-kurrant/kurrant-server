package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "application_form__spot")
public class CorporationApplicationFormSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @NotNull
    @Column(name = "emb_address", nullable = false)
    private Address address;

    @NotNull
    @Convert(converter = DiningTypeConverter.class)
    @Column(name = "e_dining_type", nullable = false)
    private DiningType diningType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "application_form__corporation_id")
    @JsonBackReference(value = "application_form__corporation_fk")
    private CorporationApplicationForm corporationApplicationForm;

    @Builder
    public CorporationApplicationFormSpot(String name, Address address, DiningType diningType) {
        this.name = name;
        this.address = address;
        this.diningType = diningType;
    }

    public void setCorporationApplicationForm(CorporationApplicationForm corporationApplicationForm) {
        this.corporationApplicationForm = corporationApplicationForm;
    }
}