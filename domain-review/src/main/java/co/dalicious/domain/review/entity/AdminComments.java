package co.dalicious.domain.review.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminComments extends Comments{
    @Builder
    public AdminComments(String content, Reviews reviews) {
        super(content, reviews);
    }

    public void updateAdminComment(String content) {
        super.updateComment(content);
    }
}
