package co.dalicious.domain.group.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(name = "client__apartment")
public class ClientApartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "family_count")
    private Integer familyCount;

    @Column(name = "manager_id")
    private Long managerId;

    @Size(max = 255)
    @NotNull
    @Column(name = "emb_address", nullable = false)
    private String embAddress;

    @Size(max = 8)
    @NotNull
    @Column(name = "e_dining_type", nullable = false, length = 8)
    private String eDiningType;

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

    @Builder
    public ClientApartment(String name, Integer familyCount, Long managerId, String embAddress, String eDiningType, LocalDate startDate, String deliveryTime) {
        this.name = name;
        this.familyCount = familyCount;
        this.managerId = managerId;
        this.embAddress = embAddress;
        this.eDiningType = eDiningType;
        this.startDate = startDate;
        this.deliveryTime = deliveryTime;
    }
}