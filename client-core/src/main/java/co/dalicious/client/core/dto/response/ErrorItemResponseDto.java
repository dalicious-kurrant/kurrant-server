package co.dalicious.client.core.dto.response;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorItemResponseDto {
  @NotNull
  private String code;

  @NotNull
  private String message;
}
