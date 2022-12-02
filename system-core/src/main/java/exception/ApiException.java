package exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

  private static final long serialVersionUID = -529464181135477878L;

  private final ExceptionEnum error;

  public ExceptionEnum getExceptionEnum() {
    return this.error;
  }

  public ApiException(ExceptionEnum e) {
    super(e.getMessage());
    this.error = e;
  }
}
