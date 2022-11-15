package io.corretto.client.core.dto.response;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SuccessResponseDto {
  @NotNull
  private Boolean success;
}
