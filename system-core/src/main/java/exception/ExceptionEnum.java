package exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ExceptionEnum {
	/* E400 */
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "E4000000", "잘못된 요청입니다."),
	UNCAUGHT_EXCEPTION(HttpStatus.BAD_REQUEST, "E4000001", "uncaughtException"),
	DOSE_NOT_SATISFY_PASSWORD_PATTERN_REQUIREMENT(HttpStatus.BAD_REQUEST, "E4000002", "비밀번호는 8~32자리의 영문자, 숫자, 특수문자를 조합하여 설정해야 합니다."),
	DOSE_NOT_CORRESPOND_CERTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "E4000003", "인증 타입이 일치하지 않습니다"),
	DUPLICATED_MEMBERSHIP(HttpStatus.UNAUTHORIZED, "E4000004", "멤버십이 여러개 존재합니다. 확인해주세요."),
	CANNOT_CONNECT_SNS(HttpStatus.BAD_REQUEST, "E4000005", "소셜 로그인 인증에 실패하였습니다."),
	/* E401 */
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E4010000", "인증되지 않은 사용자입니다."),
	PASSWORD_DOES_NOT_MATCH(HttpStatus.UNAUTHORIZED, "E4010001", "비밀번호가 일치하지 않습니다."),

	/* E403 */
	FORBIDDEN(HttpStatus.FORBIDDEN, "E4030000", "접근 권한이 없습니다."),
	SELL_STATUS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "E4030001", "sellStatusNotAllowed"),

	/* E404 */
	NOT_FOUND(HttpStatus.NOT_FOUND, "E4040000", "notFound"),
	ROUTER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040001", "routerNotFound"),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040002", "productNotFound"),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040003", "resourceNotFound"),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040004", "일치하는 유저를 찾을 수 없습니다."),
	CERTIFICATION_NUMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040004", "인증번호가 일치하지 않습니다."),
	SNS_PLATFORM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040005", "일치하는 SNS 플랫폼을 찾을 수 없습니다."),
	GENERAL_PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040005", "이메일/비밀번호 설정이 필요합니다."),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040006", "파일을 찾을 수 없습니다."),
	MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040007", "멤버십을 찾을 수 없습니다."),

	/* E409 */
	ALREADY_EXISTING_USER(HttpStatus.CONFLICT, "E4090001", "이미 존재하는 유저입니다."),

  	/* E500 */
	FAIL_TO_SEND_CERTIFICATION_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR, "E5000001", "failToSendCertificationNumber"),
	FAIL_TO_CONVERT_MULTIPART_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "E5000002", "Multipart -> File 변환에 실패하였습니다."),
	PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5000004", "결제에 실패하였습니다."),
  	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5000000", "internalServerError"),

  	/* E422 */
  	UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "E4220000", "이미 존재하는 유저입니다.");

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
