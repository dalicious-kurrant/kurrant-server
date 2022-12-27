package co.dalicious.domain.application_form.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "application_form__meal_detail")
public class ApplicationFormMealDetail {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "e_dining_type", nullable = false, length = 8)
    private String eDiningType;

    @NotNull
    @Column(name = "min_price", nullable = false, precision = 15)
    private BigDecimal minPrice;

    @NotNull
    @Column(name = "max_price", nullable = false, precision = 15)
    private BigDecimal maxPrice;

    @NotNull
    @Column(name = "daily_support_price", nullable = false, precision = 15)
    private BigDecimal dailySupportPrice;

    @NotNull
    @Column(name = "expected_people", nullable = false)
    private Integer expectedPeople;

    @NotNull
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @NotNull
    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Size(max = 255)
    @Column(name = "etc")
    private String etc;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_form__corporation_id", nullable = false)
    private ApplicationFormCorporation applicationFormCorporation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEDiningType() {
        return eDiningType;
    }

    public void setEDiningType(String eDiningType) {
        this.eDiningType = eDiningType;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getDailySupportPrice() {
        return dailySupportPrice;
    }

    public void setDailySupportPrice(BigDecimal dailySupportPrice) {
        this.dailySupportPrice = dailySupportPrice;
    }

    public Integer getExpectedPeople() {
        return expectedPeople;
    }

    public void setExpectedPeople(Integer expectedPeople) {
        this.expectedPeople = expectedPeople;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public ApplicationFormCorporation getApplicationFormCorporation() {
        return applicationFormCorporation;
    }

    public void setApplicationFormCorporation(ApplicationFormCorporation applicationFormCorporation) {
        this.applicationFormCorporation = applicationFormCorporation;
    }

}