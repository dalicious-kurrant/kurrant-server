package co.dalicious.domain.payment.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
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
    KBKOOKMIN("국민KB카드", "381","카드결제"),
    BCCARD("BC카드", "361","카드결제"),
    GWANGJUCARD("광주카드", "364","카드결제"),
    SAMSUNGCARD("삼성카드", "365","카드결제"),
    SHINHANCARD("신한카드", "366","카드결제"),
    HYUNDAICARD("현대카드", "367","카드결제"),
    LOTTECARD("롯데카드", "368","카드결제"),
    SUHYEOPCARD("수협카드", "369","카드결제"),
    CITICARD("씨티카드", "370","카드결제"),
    NHCARD("NH카드", "371","카드결제"),
    JEONBUKCARD("전북카드", "372","카드결제"),
    JEJUCARD("제주카드", "373","카드결제"),
    HANASKCARD("하나SK카드", "374","카드결제"),
    KDBSANUPCARD("KDB산업은행카드", "002","카드결제"),
    WOORICARD("우리카드", "041","카드결제"),
    SAEMAULCARD("새마을금고카드", "045","카드결제"),
    SHINHYEOPCARD("신협카드","048", "카드결제"),
    POSTCARD("우체국카드","071", "카드결제"),
    KBANKCARD("케이뱅크카드","089", "카드결제"),
    KAKAOBANKCARD("카카오뱅크카드","090", "카드결제"),


    TOSSPAY("토스페이", "TOSSPAY", "간편결제"),
    NAVERPAY("네이버페이", "NAVERPAY", "간편결제"),
    SAMSUNGPAY("삼성페이", "SAMSUNGPAY", "간편결제"),
    LPAY("엘페이", "LPAY", "간편결제"),
    KAKAOPAY("카카오페이", "KAKAOPAY", "간편결제"),
    PAYCO("페이코", "PAYCO", "간편결제"),
    LGPAY("LG페이", "LGPAY", "간편결제"),
    SSG("SSG페이", "SSG", "간편결제"),
    NULL("없음", "NULL", "카드결제"),

    MINGLE_BC("MINGLE_비씨", "MINGLE_01", "카드결제"),
    MINGLE_KB("MINGLE_국민", "MINGLE_02", "카드결제"),
    MINGLE_HANA("MINGLE_하나", "MINGLE_03", "카드결제"),
    MINGLE_SAMSUNG("MINGLE_삼성", "MINGLE_04", "카드결제"),
    MINGLE_SHINHAN("MINGLE_신한", "MINGLE_06", "카드결제"),
    MINGLE_HYUNDAI("MINGLE_현대", "MINGLE_07", "카드결제"),
    MINGLE_LOTTE("MINGLE_롯데", "MINGLE_08", "카드결제"),
    MINGLE_CITY("MINGLE_시티", "MINGLE_11", "카드결제"),
    MINGLE_NH("MINGLE_NH농협", "MINGLE_12", "카드결제"),
    MINGLE_SUHYEOP("MINGLE_수협", "MINGLE_13", "카드결제"),
    MINGLE_URI("MINGLE_우리", "MINGLE_15", "카드결제"),
    MINGLE_GWANGJU("MINGLE_광주", "MINGLE_21", "카드결제"),
    MINGLE_JEONBUK("MINGLE_전북", "MINGLE_22", "카드결제"),
    MINGLE_JEJU("MINGLE_제주", "MINGLE_23", "카드결제"),
    MINGLE_VISA("MINGLE_해외비자", "MINGLE_25", "카드결제"),
    MINGLE_MASTER("MINGLE_해외마스터", "MINGLE_26", "카드결제"),
    MINGLE_DINERS("MINGLE_해외다이너스", "MINGLE_27", "카드결제"),
    MINGLE_AMAX("MINGLE_해외AMAX", "MINGLE_28", "카드결제"),
    MINGLE_JCB("MINGLE_해외JCB", "MINGLE_29", "카드결제"),
    MINGLE_OVERSEAS("MINGLE_해외", "MINGLE_30", "카드결제"),
    MINGLE_POST("MINGLE_우체국", "MINGLE_32", "카드결제"),
    MINGLE_MG("MINGLE_MG새마을카드", "MINGLE_33", "카드결제"),
    MINGLE_CHINA("MINGLE_중국은행체크", "MINGLE_34", "카드결제"),
    MINGLE_UNIONPAY("MINGLE_은련", "MINGLE_38", "카드결제"),
    MINGLE_SHINHYEOP("MINGLE_신협", "MINGLE_41", "카드결제"),
    MINGLE_SAVINGBANK("MINGLE_저축은행", "MINGLE_42", "카드결제"),
    MINGLE_KDB("MINGLE_KDB산업", "MINGLE_43", "카드결제"),
    MINGLE_KAKAOBANK("MINGLE_카카오뱅크", "MINGLE_44", "카드결제"),
    MINGLE_KBANK("MINGLE_케이뱅크", "MINGLE_45", "카드결제"),
    MINGLE_KAKAOMONEY("MINGLE_카카오머니", "MINGLE_46", "카드결제"),
    MINGLE_SSGMONEY("MINGLE_SSG머니", "MINGLE_47", "카드결제"),
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

    public static PaymentCompany ofValue(String value){
        return Arrays.stream(PaymentCompany.values())
                .filter(v -> v.getPaymentCompany().equals(value))
                .findAny()
                .orElse(NULL);
    }

    public static PaymentCompany ofMingleCode(String code) {
        return Arrays.stream(PaymentCompany.values())
                .filter(v -> v.getCode().equals("MINGLE_" + code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public String getPaymentCompany() {
        return paymentCompany.contains("MINGLE_") ? paymentCompany.replaceFirst("MINGLE_", "") : paymentCompany;
    }
}
