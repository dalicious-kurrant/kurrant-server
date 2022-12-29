package co.dalicious.domain.application_form.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "application_form__spot")
public class ApplicationFormSpot {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @NotNull
    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_form__corporation_id", nullable = false)
    private ApplicationFormCorporation applicationFormCorporation;

    @Size(max = 255)
    @NotNull
    @Column(name = "emb_address", nullable = false)
    private String embAddress;

    @Size(max = 8)
    @NotNull
    @Column(name = "e_dining_type", nullable = false, length = 8)
    private String eDiningType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationFormCorporation getApplicationFormCorporation() {
        return applicationFormCorporation;
    }

    public void setApplicationFormCorporation(ApplicationFormCorporation applicationFormCorporation) {
        this.applicationFormCorporation = applicationFormCorporation;
    }

    public String getEmbAddress() {
        return embAddress;
    }

    public void setEmbAddress(String embAddress) {
        this.embAddress = embAddress;
    }

    public String getEDiningType() {
        return eDiningType;
    }

    public void setEDiningType(String eDiningType) {
        this.eDiningType = eDiningType;
    }

}