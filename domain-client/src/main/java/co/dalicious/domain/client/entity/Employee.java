package co.dalicious.domain.client.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "client__admin_employee")
public class Employee{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고객사 페이지에서 등록하는 유저")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "corporation_fk")
    @Comment("기업")
    private Corporation corporation;


    @Comment("사원 이름")
    private String name;

    @Comment("사원 이메일")
    private String email;

    @Comment("사원 전화번호")
    private String phone;

    @Builder
    public Employee(String name,String email,String phone, Corporation corporation){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.corporation = corporation;

    }
}
