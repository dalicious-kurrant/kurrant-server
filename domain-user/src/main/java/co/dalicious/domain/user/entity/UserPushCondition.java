package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.PushConditionConverter;
import co.dalicious.domain.user.entity.enums.PushCondition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class UserPushCondition {

    @Convert(converter = PushConditionConverter.class)
    @Column(name = "push_condition")
    @Comment("푸시 알림 조건")
    private PushCondition pushCondition;

    @Column(name = "is_active")
    @Comment("알림 활성 상태")
    private Boolean isActive;

    @Builder
    public UserPushCondition(PushCondition pushCondition, Boolean isActive) {
        this.pushCondition = pushCondition;
        this.isActive = isActive;
    }
}
