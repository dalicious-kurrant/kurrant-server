package co.dalicious.domain.makers.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigInteger;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "makers__makers")
public class Makers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name= "id", columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;
    @Column(name = "name")
    private String name;

    @Builder
    Makers(BigInteger id, String name){
        this.id = id;
        this.name = name;
    }
}
