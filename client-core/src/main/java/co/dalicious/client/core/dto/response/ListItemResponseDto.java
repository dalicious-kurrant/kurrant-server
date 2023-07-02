package co.dalicious.client.core.dto.response;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Getter
@Builder
@AllArgsConstructor
public class ListItemResponseDto<T> {
  @Min(0)
  @NotNull
  private Integer limit;

  @Min(0)
  @NotNull
  private Long offset;

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
  private List<T> items;

  private Boolean isLast;
}
