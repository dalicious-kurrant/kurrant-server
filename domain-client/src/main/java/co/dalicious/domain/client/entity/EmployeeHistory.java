package co.dalicious.domain.client.entity;

import co.dalicious.domain.client.entity.enums.EmployeeHistoryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "client__admin_employee_history")
public class EmployeeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("유저 등록/삭제 히스토리 ID")
    @Column(columnDefinition = "BIGINT UNSIGNED")
    private BigInteger id;

    @Comment(value = "이름")
    private String name;

    @Comment(value = "이메일")
    private String email;

    @Comment(value = "휴대폰번호")
    private String phone;

    @ColumnDefault("0")
    private EmployeeHistoryType type;

    @Comment("삭제된 유저의 아이디")
    private BigInteger userId;

    @Builder
    public EmployeeHistory(String name, String email, String phone, EmployeeHistoryType type, BigInteger userId){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.type = type;
        this.userId = userId;
    }

}
