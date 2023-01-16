package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Apartment;
import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.converter.ClientStatusConverter;
import co.dalicious.domain.user.entity.enums.ClientStatus;
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
@Table(name = "user__user_group")
public class UserGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED")
    @Comment("유저 그룹 정보 PK")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn
    @JsonManagedReference(value = "group_fk")
    @Comment("그룹 정보 FK")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonManagedReference(value = "user_fk")
    @Comment("유저 정보 FK")
    private User user;

    @Convert(converter = ClientStatusConverter.class)
    @Comment("가입/탈퇴 상태")
    @Column(nullable = false)
    private ClientStatus clientStatus = ClientStatus.BELONG;

    public void updateStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    @Builder
    public UserGroup(Group group, User user, ClientStatus clientStatus) {
        this.group = group;
        this.user = user;
        this.clientStatus = clientStatus;
    }
}
