package co.dalicious.domain.client.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("고객사 페이지에서 등록하는 유저")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonManagedReference(value = "corporation_fk")
    @Comment("기업")
    private Corporation corporation;

    @NotNull
    @Comment("사원 이름")
    private String name;

    @Comment("사원 이메일")
    private String email;

    @Comment("사원 전화번호")
    private String phone;
}
