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
	CHANGED_PASSWORD_SAME(HttpStatus.BAD_REQUEST, "E4000006", "전과 같은 비밀번호는 사용할 수 없습니다."),
	IS_NOT_APPROPRIATE_EMPLOYEE_COUNT(HttpStatus.BAD_REQUEST, "E4000007", "사원의 수는 0 이상이어야합니다."),
	LAST_ORDER_TIME_PASSED(HttpStatus.BAD_REQUEST, "E4000008", "상품 주문 가능시간이 지났습니다."),
	SOLD_OUT(HttpStatus.BAD_REQUEST, "E4000009", "품절된 상품입니다."),
	NOT_MATCHED_PRICE(HttpStatus.BAD_REQUEST, "E4000010", "금액이 일치하지 않습니다."),
	NOT_MATCHED_DELIVERY_FEE(HttpStatus.BAD_REQUEST, "E4000011", "배송비가 일치하지 않습니다."),
	NOT_MATCHED_SUPPORT_PRICE(HttpStatus.BAD_REQUEST, "E4000012", "회사 지원금이 일치하지 않습니다."),
	NOT_MATCHED_ITEM_COUNT(HttpStatus.BAD_REQUEST, "E4000012", "요청 수량이 일치하지 않습니다"),
	HAS_LESS_POINT_THAN_REQUEST(HttpStatus.BAD_REQUEST, "E4000013", "포인트 잔액이 부족합니다."),
	OVER_ITEM_CAPACITY(HttpStatus.BAD_REQUEST, "E4000014", "상품의 개수가 부족합니다."),
	ALREADY_EXISTING_MEMBERSHIP(HttpStatus.BAD_REQUEST, "E4000015", "이미 멤버십이 존재합니다."),
	ALREADY_EXISTING_NAME(HttpStatus.BAD_REQUEST, "E4000015", "이미 유저 이름이 존재합니다."),
	/* E401 */
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E4010000", "인증되지 않은 사용자입니다."),
	PASSWORD_DOES_NOT_MATCH(HttpStatus.UNAUTHORIZED, "E4010001", "비밀번호가 일치하지 않습니다."),

	/* E403 */
	FORBIDDEN(HttpStatus.FORBIDDEN, "E4030000", "접근 권한이 없습니다."),
	SELL_STATUS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "E4030001", "sellStatusNotAllowed"),
	REFRESH_TOKEN_ERROR(HttpStatus.FORBIDDEN, "E4030002", "유효하지 않은 Refresh Token입니다."),
	ACCESS_TOKEN_ERROR(HttpStatus.FORBIDDEN, "E4030003", "유효하지 않은 Access Token입니다."),

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
	APPLICATION_FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040008", "스팟 신청 내역을 찾을 수 없습니다."),
	SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040009", "스팟을 찾을 수 없습니다."),
	CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040010", "그룹을 찾을 수 없습니다."),
	NOT_SET_SPOT(HttpStatus.NOT_FOUND, "E4040011", "등록되지 않은 스팟입니다."),
	ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040012", "주문하려는 상품에 문제가 있습니다"),
	DAILY_FOOD_NOT_FOUND(HttpStatus.NOT_FOUND, "E4040013", "식단을 찾을 수 없습니다"),
	PRICE_INTEGRITY_ERROR(HttpStatus.NOT_FOUND, "E4040014", "주문 요청 가격과 실제 가격에 차이가 있습니다."),
	NOT_FOUND_DELIVERY_FEE_POLICY(HttpStatus.NOT_FOUND, "E4040015", "배송비 정책을 가져올 수 없습니다."),
	NOT_FOUND_MEAL_INFO(HttpStatus.NOT_FOUND, "E4040015", "식사 정보를 가져올 수 없습니다."),
	ALREADY_READ(HttpStatus.NOT_FOUND,"E4040015", "읽을 알람이 없습니다..")

	/* E409 */,
	ALREADY_EXISTING_USER(HttpStatus.CONFLICT, "E4090001", "이미 존재하는 유저입니다."),
	ALREADY_EXISTING_GROUP(HttpStatus.CONFLICT, "E4090001", "이미 그룹에 가입된 유저입니다."),

  	/* E500 */
	FAIL_TO_SEND_CERTIFICATION_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR, "E5000001", "failToSendCertificationNumber"),
	FAIL_TO_CONVERT_MULTIPART_FILE(HttpStatus.INTERNAL_SERVER_ERROR, "E5000002", "Multipart -> File 변환에 실패하였습니다."),
	PAYMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5000004", "결제에 실패하였습니다."),
	PAYMENT_CANCELLATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E5000008", "결제 취소에 실패하였습니다."),
	FAIL_TO_CREDITCARD_REGIST(HttpStatus.INTERNAL_SERVER_ERROR, "E5000003", "결제카드 등록에 실패하였습니다."),
	SPOT_DATA_INTEGRITY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"E5000005", "유저의 그룹/스팟 상태에 오류가 있습니다."),
	UPDATE_ORDER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"E5000006", "주문서 수정에 실패했습니다."),
	DUPLICATE_CANCELLATION_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR,"E5000007", "취소 실패 : 이미 취소된 결제건입니다."),
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
