package co.dalicious.system.util;

import lombok.Builder;
import lombok.Data;

@Data
public class ResponseMessage {
    private Boolean result;
    private String message;

    private Object data;

    @Builder
    public ResponseMessage(Boolean result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    @Builder
    public ResponseMessage(Boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
