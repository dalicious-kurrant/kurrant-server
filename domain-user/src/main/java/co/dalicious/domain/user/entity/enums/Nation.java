package co.dalicious.domain.user.entity.enums;

import exception.ApiException;
import exception.ExceptionEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Nation {
/*
    REPUBLIC_OF_KOREA("South Korea", 1),
    GHANA("Ghana", 2),
    GABONG("Gabon",3),
    GUYANA("Guyana", 4),
    GAMBIA("Gambia", 5),
    GRENADA("Grenada", 6),
    GUATEMALA("Guatemala", 7),
    GUAM("Guam", 8),
    HOLY_SEE("Vatican City State", 9),
    GREECE("Greece", 10),
    GREENLAND("Greenland", 11),
    GUINEA_BISSAU("Guinea-Bissau", 12),
    GUINEA("Guinea", 13),
    NAMIBIA("Namibia", 14),
    NAURU("Nauru", 15),
    NIGERIA("Nigeria", 16),
    SOUTH_SUDAN("South Sudan", 17),
    SOUTH_AFRICA("South Africa", 18),
    SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("South Georgia and the South Sandwich Islands", 19),
    NETHERLANDS("Netherlands", 20),
    NETHERLANDS_ANTILLES("Netherlands Antilles", 21),
    NEPAL("Nepal", 22),
    NORWAY("Norway", 23),
    NORFOLK_ISLAND("Norfolk Island", 24),
    NEW_CALEDONIA("New Caledonia", 25),
    NEW_ZEALAND("New Zealand", 26),
    NIUE("Niue", 27),
    NIGER("Niger", 28),
    NICARAGUA("Nicaragua", 29),
    TAIWAN("Taiwan", 30),
    DENMARK("Denmark", 31),
    DOMINICA("Dominica", 32),
    DOMINICAN_REPUBLIC("Dominican Republic", 33),
    GERMANY("Germany", 34),
    LAOS("Laos", 35),
    LIBERIA("Liberia", 36),
    LATVIA("Latvia", 37),
    RUSSIAN_FEDERATION("Russia", 38),
    LEBANON("Lebanon", 39),
    LESOTHO("Lesotho", 40),
    ROMANIA("Romania", 41),
    LUXEMBOURG("Luxembourg", 42),
    RWANDA("Rwanda", 43),
    LIBYA("Libya", 44),
    LITHUANIA("Lithuania", 45),
    LIECHTENSTEIN("Liechtenstein", 46),
    MADAGASCAR("Madagascar", 47),
    MARSHALL_ISLANDS("Marshall Islands", 48),
    MACEDONIA("North Macedonia", 49),
    MICRONESIA("Micronesia", 50),
    MACAO("Macao", 51),
    MARTINIQUE("Martinique", 52),
    MALAWI("Malawi", 53),
    MALAYSIA("Malaysia", 54),
    MALI("Mali", 55),
    ISLE_OF_MAN("Isle of Man", 56),
    MAYOTTE("Mayotte", 57),
    MEXICO("Mexico", 58),
    MONACO("Monaco", 59),
    MOROCCO("Morocco", 60),
    MAURITIUS("Mauritius", 61),
    MAURITANIA("Mauritania", 62),
    모잠비크("모잠비크", 63),
    몬테네그로("몬테네그로", 64),
    몬트세라트("몬트세라트", 65),
    몰도바("몰도바", 66),
    몰디브("몰디브", 67),
    몰타("몰타", 68),
    몽골("몽골", 69),
    미국("미국", 70),
    미드웨이_군도("미드웨이 군도", 71),
    미령_버진군도("미령 버진군도", 72),
    미얀마("미얀마", 73),
    바누아투("바누아투", 74),
    바레인("바레인", 75),
    바베이도스("바베이도스", 76),
    바하마("바하마", 77),
    방글라데시("방글라데시", 78),
    버뮤다("버뮤다", 79),
    베네수엘라("베네수엘라", 80),
    베냉("베냉", 81),
    벨라루스("벨라루스", 82),
    베트남("베트남", 83),
    벨기에("벨기에", 84),
    벨리즈("벨리즈", 85),
    보네르_신트외스타티위스("보네르 신트외스타티위스", 86),
    보비트군도("보비트 군도", 87),
    보스니아_헤르체고비나("보스니아-헤르체고비나", 88),
    보츠와나("보츠와나", 89),
    볼리비아("볼리비아", 90),
    부룬디("부룬디", 91),
    부르키나파소("부르키나 파소", 92),
    부탄("부탄", 93),
    북마리아나군도("북마리아나 군도", 94),
    북한("북한", 95),
    불가리아("불가리아", 96),
    불령가이아나("불령 가이아나", 97),
    불령남부지역("불령 남부지역", 98),
    불령리유니온코모도제도("불령 리유니온 코모도 제도", 99),
    FRENCH_POLYNESIA("프랑스령 폴리네시아", 100),
    BRAZIL("브라질", 101),
    BRUNEI("브루나이", 100),
    SAMOA("사모아", 101),
    SAUDI_ARABIA("사우디아라비아", 102),
    CYPRUS("사이프러스", 103),
    SAN_MARINO("산마리노", 104),
    SAO_TOME_AND_PRINCIPE("상토메 프린스페", 105),
    WESTERN_SAHARA("서사하라", 106),
    SENEGAL("세네갈", 107),
    SERBIA("세르비아", 108),
    SEYCHELLES("세이쉘", 109),
    SAINT_LUCIA("세인트 루시아", 110),
    SAINT_MARTIN("세인트 마틴", 111),
    SAINT_BARTHELEMY("세인트 바르탤르미", 112),
    SAINT_VINCENT_AND_THE_GRENADINES("세인트 빈센트 그레나딘", 113),
    SAINT_KITTS_AND_NEVIS("세인트 키츠 네비스", 114),
    SAINT_PIERRE_AND_MIQUELON("세인트 피레 미켈론", 115),
    SAINT_HELENA("세인트 헬레나", 116),
    SOMALIA("소말리아", 117),
    SOLOMON_ISLANDS("솔로몬 군도", 118),
    SUDAN("수단", 119),
    SURINAME("수리남", 120),
    SRI_LANKA("스리랑카", 121),
    SVALBARD_AND_JAN_MAYEN("스발비드 군도", 122),
    SWAZILAND("스와질랜드", 123),
    SWEDEN("스웨덴", 124),
    SWITZERLAND("스위스", 125),
    SPAIN("스페인", 126),
    SLOVAKIA("슬로바키아", 127),
    SLOVENIA("슬로베니아", 128),
    SYRIA("시리아", 129),
    SIERRA_LEONE("시에라 리온", 130),
    SINGAPORE("싱가포르", 131),
    UNITED_ARAB_EMIRATES("아랍에미리트 연합", 132),
    ARUBA("아루바", 133),
    ARMENIA("아르메니아", 134),
    ARGENTINA("아르헨티나", 135),
    AMERICAN_SAMOA("아메리칸 사모아", 136),
    ICELAND("아이슬란드", 137),
    HAITI("아이티", 138),
    IRELAND("아일랜드", 139),
    AZERBAIJAN("아제르바이잔", 140),
    AFGHANISTAN("아프카니스탄", 141),
    ANDORRA("안도라", 142),
    ANTARCTICA("안타티카", 143),
    ANTIGUA_AND_BARBUDA("안티가 바부다", 144),
    ÅLAND_ISLANDS("알랜드 군도", 145),
    ALBANIA("알바니아", 146),
    ALGERIA("알제리", 147),
    ANGOLA("앙골라", 148),
    ANGUILLA("앙귈라", 149),
    ERITREA("에리트리아", 150),
    ESTONIA("에스토니아", 151),
    ECUADOR("에쿠아도르", 152),
    ETHIOPIA("에티오피아", 153),
    EL_SALVADOR("엘살바도르", 154),
    UNITED_KINGDOM("영국", 155),
    BRITISH_VIRGIN_ISLANDS("영령 버진군도", 156),
    BRITISH_INDIAN_OCEAN_TERRITORY("영령 인도양", 157),
    CAYMAN_ISLANDS("영령 캐이맨 군도", 158),
    YEMEN("예맨", 159),
    OMAN("오만", 160),
    AUSTRIA("오스트리아", 161),
    HONDURAS("온두라스", 162),
    WALLIS_AND_FUTUNA("왈라스 & 퓨투나 군도", 163),
    JORDAN("요르단", 164),
    UGANDA("우간다", 165),
    URUGUAY("우루과이", 166),
    UZBEKISTAN("우즈베키스탄", 167),
    UKRAINE("우크라이나", 168),
    WAKE_ISLAND("웨이크 아일랜드", 169),
    YUGOSLAVIA("유고", 170),
    IRAQ("이라크", 171),
    IRAN("이란", 172),
    ISRAEL("이스라엘", 173),
    EGYPT("이집트", 174),
    ITALY("이탈리아", 175),
    INDIA("인도", 176),
    INDONESIA("인도네시아", 177),
    JAPAN("일본", 178),
    JAMAICA("자마이카", 179),
    ZAIRE("자이르", 180),
    ZAMBIA("잠비아", 181),
    JERSEY("저어지", 182),
    EQUATORIAL_GUINEA("적도 기니", 183),
    GEORGIA("조지아", 184),
    JOHNSTON_ISLAND("존스톤 아일랜드", 185),
    CHINA("중국", 186),
    NEUTRAL_ZONE("중립지대", 187),
    CENTRAL_AFRICAN_REPUBLIC("중앙아프리카공화국", 188),
    DJIBOUTI("지부티", 189),
    GIBRALTAR("지브랄타", 190),
    ZIMBABWE("짐바브웨", 191),
    CHAD("챠드", 192),
    CZECH_REPUBLIC("체코공화국", 193),
    CHILE("칠레", 194),
    CAMEROON("카메룬", 195),
    CAPE_VERDE("카보 베르데", 196),
    KAZAKHSTAN("카자흐스탄", 197),
    QATAR("카타르", 198),
    CAMBODIA("캄보디아", 199),
    CANADA("캐나다", 200),
    CANTON_ISLAND("캔톤아일랜드", 201),
    KENYA("케냐", 202),
    COMOROS("코모로스", 203),
    COCOS_KEELING_ISLANDS("코스 군도", 204),
    COSTA_RICA("코스타리카", 205),
    COTE_D_IVOIRE("코트디봐르", 206),
    COLOMBIA("콜롬비아", 207),
    CONGO("콩고", 208),
    DEMOCRATIC_REPUBLIC_OF_CONGO("콩고민주공화국", 209),
    CUBA("쿠바", 210),
    KUWAIT("쿠웨이트", 211),
    COOK_ISLANDS("쿡 아일랜드", 212),
    CURACAO("큐라소", 213),
    CROATIA("크로아티아", 214),
    CHRISTMAS_ISLAND("크리스마스 아일랜드", 215),
    KYRGYZSTAN("키르기스스탄", 216),
    KIRIBATI("키리바티", 217),
    TAJIKISTAN("타지크", 218),
    TANZANIA("탄자니아", 219),
    THAILAND("태국", 220),
    TOGO("토고", 221),
    TOKELAU("토켈라우", 222),
    TONGA("통가", 223),
    TURKS_AND_CAICOS_ISLANDS("투르크 & 카이코스 군도", 224),
    TURKMENISTAN("투르크멘", 225),
    TUVALU("투발루", 226),
    TUNISIA("튀니지", 227),
    TURKEY("튀르키예", 228),
    TRINIDAD_AND_TOBAGO("트리니다드 토바고", 229),
    TIMOR_LESTE("티모르", 230),
    PANAMA("파나마", 231),
    PANAMA_CANAL_ZONE("파나마운하지역", 232),
    PARAGUAY("파라과이", 233),
    PARACEL_ISLANDS("파로에 군도", 234),
    PAKISTAN("파키스탄", 235),
    PAPUA_NEW_GUINEA("파푸아 뉴기니", 236),
    PALAU("팔라우", 237),
    PALESTINE("팔레스타인 해방기구", 238);

*/





    /*
나우루
나이지리아
남수단
남아프리카공화국
남조지아 & 남샌드위치 군도
네덜란드
네덜란드 열도
네팔
노르웨이
노폴크 아일랜드
뉴 칼레도니아
뉴질랜드
니우에
니제르
니카라과
대만
덴마크
도미니카
도미니카 공화국
독일
라오스
라이베리아
라트비아
러시아 연방
레바논
레소토
루마니아
룩셈부르그
르완다
리비아
리투아니아
리히텐슈타인
마다카스카르
마샬군도
마세도니아
마이너 아우틀링 합중국 군도
마이크로네시아
마카오
마티니크
말라위
말레이시아
말리
맨섬
메요트
멕시코
모나코
모로코
모리셔스
모리타니
모잠비크
몬테네그로
몬트세라트
몰도바
몰디브
몰타
몽골
미국
미드웨이 군도
미령 버진군도
미얀마
바누아투
바레인
바베이도스
바하마
방글라데시
버뮤다
베네주엘라
베닝
베라루스
베트남
벨기에
벨리제
보네르 신트외스타티위스
보빗군도
보스니아-헤르체고비나
보츠와나
볼리비아
부룬디
부르키나 파소
부탄
북마리아나 군도
북한
불가리아
불령 가이아나
불령 남부지역
불령 리유니온 코모도 제도
불령 폴리네시아
브라질
브루나이
사모아
사우디아라비아
사이프러스
산마리노
상토메 프린스페
서사하라
세네갈
세르비아
세이쉘
세인트 루시아
세인트 마틴
세인트 바르탤르미
세인트 빈센트 그레나딘
세인트 키츠 네비스
세인트 피레 미켈론
세인트 헬레나
소말리아
솔로몬 군도
수단
수리남
스리랑카
스발비드 군도
스와질랜드
스웨덴
스위스
스페인
슬로바키아
슬로베니아
시리아
시에라 리온
싱가포르
아랍에미리트 연합
아루바
아르메니아
아르헨티나
아메리칸 사모아
아이슬란드
아이티
아일랜드
아제르바이잔
아프카니스탄
안도라
안타티카
안티가 바부다
알랜드 군도
알바니아
알제리
앙골라
앙귈라
에리트리아
에스토니아
에쿠아도르
에티오피아
엘살바도르
영국
영령 버진군도
영령 인도양
영령 캐이맨 군도
예맨
오만
오스트리아
온두라스
왈라스 & 퓨투나 군도
요르단
우간다
우루과이
우즈베키스탄
우크라이나
웨이크 아일랜드
유고
이라크
이란
이스라엘
이집트
이탈리아
인도
인도네시아
일본
자마이카
자이르
잠비아
저어지
적도 기니
조지아
존스톤 아일랜드
중국
중립지대
중앙아프리카공화국
지부티
지브랄타
짐바브웨
챠드
체코공화국
칠레
카메룬
카보 베르데
카자흐스탄
카타르
캄보디아
캐나다
캔톤아일랜드
케냐
코모로스
코스 군도
코스타리카
코트디봐르
콜롬비아
콩고
콩고민주공화국
쿠바
쿠웨이트
쿡 아일랜드
큐라소
크로아티아
크리스마스 아일랜드
키르기스스탄
키리바티
타지크
탄자니아
태국
토고
토켈라우
통가
투르크 & 카이코스 군도
투르크멘
투발루
튀니지
튀르키예
트리니다드 토바고
티모르
파나마
파나마운하지역
파라과이
파로에 군도
파키스탄
파푸아 뉴기니
팔라우
팔레스타인 해방기구
페루
포루투갈
포클랜드 군도
폴란드
푸에르토리코
프랑스
프랑스 메트로폴리탄
피지
피트카이른
핀란드
필리핀
한국
허드 앤 맥도날드 군도
헝가리
호주
홍콩
    *
    * */




/*
* USER_ATTENDANCE("출석 체크", 1),

ORDER_FIRST_ORDER("첫구매 이벤트",101);

    private final String condition;
    private final Integer code;

    PointCondition(String condition, Integer code) {
        this.condition = condition;
        this.code = code;
    }

    public static PointCondition ofCode(Integer code) {
        return Arrays.stream(PointCondition.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new ApiException(ExceptionEnum.ENUM_NOT_FOUND));
    }

    public static PointCondition ofValue(String value){
        return Arrays.stream(PointCondition.values())
                .filter(v -> v.getCondition().equals(value))
                .findAny()
                .orElse(null);
    }

* */

}
