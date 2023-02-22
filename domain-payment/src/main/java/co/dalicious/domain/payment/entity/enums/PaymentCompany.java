package co.dalicious.domain.payment.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaymentCompany {
    IBK_BC("기업비씨카드", "3K", "카드결제"),
    GWANGJUBANK("광주카드", "46", "카드결제"),
    LOTTE("롯데카드", "71", "카드결제"),
    KDBBANK("산업카드", "30", "카드결제"),
    BC("비씨카드", "31", "카드결제"),
    SAMSUNG("삼성카드", "51", "카드결제"),
    SAEMAUL("새마을카드", "38", "카드결제"),
    SHINHAN("신한카드", "41", "카드결제"),
    SHINHYEOP("신협카드", "62", "카드결제"),
    CITI("씨티카드", "36", "카드결제"),
    WOORI("우리카드", "33", "카드결제"),
    POST("우체국카드", "37", "카드결제"),
    SAVINGBANK("저축카드", "39", "카드결제"),
    JEONBUKBANK("전북카드", "35", "카드결제"),
    JEJUBANK("제주카드", "42", "카드결제"),
    KAKAOBANK("카카오뱅크카드", "15", "카드결제"),
    KBANK("케이뱅크카드", "3A", "카드결제"),
    TOSSBANK("토스뱅크카드", "24", "카드결제"),
    HANA("하나카드", "21", "카드결제"),
    HYUNDAI("현대카드", "61", "카드결제"),
    KOOKMIN("국민카드", "11", "카드결제"),
    NONGHYEOP("농협카드", "91", "카드결제"),
    SUHYEOP("수협카드", "34", "카드결제"),
    DINERS("다이너스카드", "6D", "카드결제"),
    DISCOVER("디스커버카드", "6I", "카드결제"),
    MASTER("마스터카드", "4M", "카드결제"),
    UNIONPAY("유니온페이카드", "3C", "카드결제"),
    AMEX("아메리칸익스프레스카드", "3C", "카드결제"),
    JCB("JCB카드", "4J", "카드결제"),
    VISA("비자카드", "4V", "카드결제"),


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
