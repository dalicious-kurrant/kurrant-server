package co.dalicious.client.core.dto.response;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateResponseDto<T> {
  @NotNull
  private T id;
}
