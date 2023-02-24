package co.kurrant.app.admin_api.dto.schedules;

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
    private Integer offset;

    @Min(0)
    @NotNull
    @Comment("현재 페이지에 담을 수 있는 최대 아이템 수")
    private Long total;

    @Min(0)
    @NotNull
    @Comment("현재 페이지의 아이템 수")
    private Integer count;

    @NotNull
    @Comment("현재 페이지의 아이템")
    private T items;
}
