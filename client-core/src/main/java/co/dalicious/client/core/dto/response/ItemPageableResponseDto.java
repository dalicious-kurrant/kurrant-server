package co.dalicious.client.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class ItemPageableResponseDto<T> {
    @Min(0)
    @NotNull
    private Integer limit;

    @Min(0)
    @NotNull
    @Comment("토탈 페이지")
    private Integer total;

    @Min(0)
    @NotNull
    @Comment("현재 페이지의 아이템 수")
    private Integer count;

    private Boolean isLast;

    @NotNull
    @Comment("현재 페이지의 아이템")
    private T items;
}
