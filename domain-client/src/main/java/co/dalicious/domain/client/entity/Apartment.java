package co.dalicious.domain.client.entity;

import co.dalicious.domain.address.entity.embeddable.Address;
import co.dalicious.system.util.DiningType;
import co.dalicious.system.util.converter.DiningTypeConverter;
import co.dalicious.system.util.converter.DiningTypesConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.time.LocalDate;

import java.sql.Timestamp;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "client__apartment")
public class Apartment extends Group{

    @Column(name = "family_count")
    private Integer familyCount;



    public Apartment(Address address, List<DiningType> diningTypes, String name, BigInteger managerId, Integer familyCount) {
        super(address, diningTypes, name, managerId);
        this.familyCount = familyCount;
    }
}