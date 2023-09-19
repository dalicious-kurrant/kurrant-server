package co.dalicious.domain.application_form.entity;

import co.dalicious.domain.application_form.entity.enums.ProgressStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;

@DynamicInsert
@DynamicUpdate
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RequestedCorporation extends RequestedPartnership {

    @Builder
    public RequestedCorporation(String username, String address, String phone, String memo, ProgressStatus progressStatus) {
        super(username, address, phone, memo, progressStatus);
    }
}
