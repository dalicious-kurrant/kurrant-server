package co.dalicious.domain.group.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(name = "client__corporation")
public class ClientCorporation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(name = "manager_id")
    private Long managerId;

    @Size(max = 8)
    @NotNull
    @Column(name = "e_dining_type", nullable = false, length = 8)
    private String eDiningType;

    @Size(max = 45)
    @Column(name = "emb_address", length = 45)
    private String embAddress;

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
    public ClientCorporation(String name, Integer employeeCount, Long managerId, String eDiningType, String embAddress, LocalDate startDate, String deliveryTime) {
        this.name = name;
        this.employeeCount = employeeCount;
        this.managerId = managerId;
        this.eDiningType = eDiningType;
        this.embAddress = embAddress;
        this.startDate = startDate;
        this.deliveryTime = deliveryTime;
    }
}