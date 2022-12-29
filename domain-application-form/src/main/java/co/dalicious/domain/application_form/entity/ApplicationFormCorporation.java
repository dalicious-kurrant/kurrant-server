package co.dalicious.domain.application_form.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Table(name = "application_form__corporation")
public class ApplicationFormCorporation {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 16)
    @NotNull
    @Column(name = "phone", nullable = false, length = 16)
    private String phone;

    @Size(max = 64)
    @NotNull
    @Column(name = "email", nullable = false, length = 64)
    private String email;

    @Size(max = 64)
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "emb_address", nullable = false)
    private String embAddress;

    @NotNull
    @Column(name = "employee_count", nullable = false)
    private Integer employeeCount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Size(max = 64)
    @NotNull
    @Column(name = "manager_name", nullable = false, length = 64)
    private String managerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmbAddress() {
        return embAddress;
    }

    public void setEmbAddress(String embAddress) {
        this.embAddress = embAddress;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

}