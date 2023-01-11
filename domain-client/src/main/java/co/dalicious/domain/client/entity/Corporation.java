package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__corporation")
public class Corporation extends Group{
    @Column(name = "employee_count")
    @Comment("사원수")
    private Integer employeeCount;

    @Column(name = "is_garbage")
    @Comment("쓰레기 수거 서비스 사용 유무")
    private Boolean isGarbage;

    @Column(name = "is_hot_storage")
    @Comment("온장고 대여 서비스 사용 유무")
    private Boolean isHotStorage;

    @Column(name = "is_setting")
    @Comment("식사 세팅 지원 서비스 사용 유무")
    private Boolean isSetting;

    @Builder
    public Corporation(Address address, List<DiningType> diningTypes, String name, BigInteger managerId, Integer employeeCount, Boolean isGarbage, Boolean isHotStorage, Boolean isSetting) {
        super(address, diningTypes, name, managerId);
        this.employeeCount = employeeCount;
        this.isGarbage = isGarbage;
        this.isHotStorage = isHotStorage;
        this.isSetting = isSetting;
    }
}