package io.corretto.client.core.dto.response;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
  private Long total;

  @Min(0)
  @NotNull
  private Integer count;

  @NotNull
  private List<T> items;
}
