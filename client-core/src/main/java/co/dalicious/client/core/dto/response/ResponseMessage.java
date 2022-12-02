package co.dalicious.client.core.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class ResponseMessage {
    private String message;

    private Object data;

    @Builder
    public ResponseMessage(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    @Builder
    public ResponseMessage(String message) {
        this.message = message;
    }
}
