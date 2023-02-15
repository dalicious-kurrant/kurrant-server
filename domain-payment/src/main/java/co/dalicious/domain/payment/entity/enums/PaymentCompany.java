package co.dalicious.domain.payment.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentCompany {
    IBK_BC("기업비씨", "3K", "카드결제"),
    GWANGJUBANK("광주", "46", "카드결제"),
    LOTTE("롯데", "71", "카드결제"),
    KDBBANK("산업", "30", "카드결제"),
    BC("비씨", "31", "카드결제"),
    SAMSUNG("삼성", "51", "카드결제"),
    SAEMAUL("새마을", "38", "카드결제"),
    SHINHAN("신한", "41", "카드결제"),
    SHINHYEOP("신협", "62", "카드결제"),
    CITI("씨티", "36", "카드결제"),
    WOORI("우리", "33", "카드결제"),
    POST("우체국", "37", "카드결제"),
    SAVINGBANK("저축", "39", "카드결제"),
    JEONBUKBANK("전북", "35", "카드결제"),
    JEJUBANK("제주", "42", "카드결제"),
    KAKAOBANK("카카오뱅크", "15", "카드결제"),
    KBANK("케이뱅크", "3A", "카드결제"),
    TOSSBANK("토스뱅크", "24", "카드결제"),
    HANA("하나", "21", "카드결제"),
    HYUNDAI("현대", "61", "카드결제"),
    KOOKMIN("국민", "11", "카드결제"),
    NONGHYEOP("농협", "91", "카드결제"),
    SUHYEOP("수협", "34", "카드결제"),
    DINERS("다이너스", "6D", "카드결제"),
    DISCOVER("디스커버", "6I", "카드결제"),
    MASTER("마스터", "4M", "카드결제"),
    UNIONPAY("유니온페이", "3C", "카드결제"),
    AMEX("아메리칸익스프레스", "3C", "카드결제"),
    JCB("JCB", "4J", "카드결제"),
    VISA("비자", "4V", "카드결제"),


    TOSSPAY("토스페이", "TOSSPAY", "간편결제"),
    NAVERPAY("네이버페이", "NAVERPAY", "간편결제"),
    SAMSUNGPAY("삼성페이", "SAMSUNGPAY", "간편결제"),
    LPAY("엘페이", "LPAY", "간편결제"),
    KAKAOPAY("카카오페이", "KAKAOPAY", "간편결제"),
    PAYCO("페이코", "PAYCO", "간편결제"),
    LGPAY("LG페이", "LGPAY", "간편결제"),
    SSG("SSG페이", "SSG", "간편결제"),
    ;

    private final String paymentCompany;
    private final String code;
    private final String paymentType;

    PaymentCompany(String cardCompany, String code, String paymentType) {
        this.paymentCompany = cardCompany;
        this.code = code;
        this.paymentType = paymentType;
    }

    public static PaymentCompany ofCode(String code) {
        return Arrays.stream(PaymentCompany.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }
}
