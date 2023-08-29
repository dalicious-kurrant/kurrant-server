package co.dalicious.domain.user.entity;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.user.converter.ClientStatusConverter;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__user_group", uniqueConstraints={@UniqueConstraint(columnNames={"group_id", "user_id"})})
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

    @CreationTimestamp
    @Column(name = "created_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) COMMENT '생성일'")
    private Timestamp createdDateTime;

    @UpdateTimestamp
    @Column(name = "updated_datetime", nullable = false, insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP(6) DEFAULT NOW(6) ON UPDATE NOW(6) COMMENT '수정일'")
    private Timestamp updatedDateTime;

    @Column(name = "memo", columnDefinition = "VARCHAR(255)")
    @Comment("메모")
    private String memo;

    public void updateStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    @Builder
    public UserGroup(Group group, User user, ClientStatus clientStatus) {
        this.group = group;
        this.user = user;
        this.clientStatus = clientStatus;
    }

    public static Long activeUserCount(LocalDate serviceDate, List<UserGroup> userGroupList) {
        return userGroupList.stream()
                .filter(v -> v.getClientStatus().equals(ClientStatus.BELONG) ||
                        (v.getClientStatus().equals(ClientStatus.WITHDRAWAL) && v.getUpdatedDateTime().compareTo(Timestamp.valueOf(serviceDate.atStartOfDay())) > 0))
                .count();
    }
}
