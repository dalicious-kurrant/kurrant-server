package exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException  extends RuntimeException {

    private static final long serialVersionUID = -32946435904897878L;
    private final HttpStatus status;
    private final String code;
    private final String error;

    public String getExceptionEnum() {
        return this.error;
    }

    public CustomException(HttpStatus status, String code, String error) {
        this.status = status;
        this.code = code;
        this.error = error;
    }
}
