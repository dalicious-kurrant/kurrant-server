package co.dalicious.client.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ExceptionEnum {
	/* E400 */
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "E4000000", "badRequest"),
	UNCAUGHT_EXCEPTION(HttpStatus.BAD_REQUEST, "E4000001", "uncaughtException"),

	/* E401 */
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E4010000", "unauthorized"),
	PASSWORD_DOES_NOT_MATCH(HttpStatus.UNAUTHORIZED, "E4010001", "passwordDoesNotMatch"),

	/* E403 */
	FORBIDDEN(HttpStatus.FORBIDDEN, "E4030000", "forbidden"),
	SELL_STATUS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "E4030001", "sellStatusNotAllowed"),

	/* E404 */
	NOT_FOUND(HttpStatus.NOT_FOUND, "E4040000", "notFound"),
	ROUTER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040001", "routerNotFound"),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040002", "productNotFound"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040003", "resourceNotFound"),

	/* E409 */
	ALREADY_EXISTING_USER(HttpStatus.CONFLICT, "E4090001", "alreadyExistingUser"),

  	/* E500 */
  	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5000000", "internalServerError"),

  	/* E422 */
  	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "E4220000", "alreadyExistingUser");

	private final HttpStatus status;
	private final String code;
	private String message;

	ExceptionEnum(HttpStatus status, String code) {
		this.status = status;
		this.code = code;
	}

	ExceptionEnum(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
