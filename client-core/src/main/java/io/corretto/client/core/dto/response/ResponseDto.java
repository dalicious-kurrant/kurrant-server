package io.corretto.client.core.dto.response;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseDto<T> {
  @NotNull
  private String id;

  private T data;

  private List<ErrorItemResponseDto> errors;
}
