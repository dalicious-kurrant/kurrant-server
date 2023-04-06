package co.dalicious.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__preference")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "BIGINT UNSIGNED", nullable = false)
    @Comment("PK")
    private BigInteger id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @JsonManagedReference(value = "user_fk")
    @Comment("유저 정보 FK")
    private User user;

    @Column(name = "breakfast_count", columnDefinition = "INT")
    @Comment("아침식사 횟수")
    private Integer breakfastCount;

    @Column(name = "midnight_snack_count", columnDefinition = "INT")
    @Comment("야식 횟수")
    private Integer midnightSnackCount;

    @Column(name = "exercise_count", columnDefinition = "INT")
    @Comment("운동 횟수")
    private Integer exerciseCount;

    @Column(name = "drink_count", columnDefinition = "INT")
    @Comment("음주 횟수")
    private Integer drinkCount;

    @Column(name = "favorite_country_food", columnDefinition = "VARCHAR(255)")
    @Comment("좋아하는 나라 음식")
    private String favoriteCountryFood;

    @Column(name = "allergy_info", columnDefinition = "VARCHAR(255)")
    @Comment("알러지 정보")
    private String allergyInfo;

    @Column(name = "is_began", columnDefinition = "Boolean")
    @Comment("비건 여부")
    private Boolean isBegan;

    @Column(name = "began_level", columnDefinition = "INT")
    @Comment("비건 정도")
    private Integer beganLevel;

    @Column(name = "is_protein", columnDefinition = "Boolean")
    @Comment("프로틴 섭취여부")
    private Boolean isProtein;

    @Column(name = "protein_scoop", columnDefinition = "INT")
    @Comment("프로틴 파우더 주간 섭취량")
    private Integer proteinScoop;

    @Column(name = "protein_bar_frequency", columnDefinition = "INT")
    @Comment("프로틴 바 주간 섭취량")
    private Integer proteinBarFrequency;

    @Column(name = "birth_year", columnDefinition = "VARCHAR(8)")
    @Comment("태어난 년도")
    private String birthYear;

    @Column(name = "birth_month", columnDefinition = "VARCHAR(8)")
    @Comment("태어난 월")
    private String birthMonth;

    @Column(name = "birth_day", columnDefinition = "VARCHAR(8)")
    @Comment("태어난 일")
    private String birthDay;

    @Column(name = "gender", columnDefinition = "INT")
    @Comment("성별")
    private Integer gender;

    @Column(name = "country", columnDefinition = "INT")
    @Comment("국가")
    private Integer country;

    @Column(name = "birth_place", columnDefinition = "INT")
    @Comment("출생지")
    private Integer birthPlace;

    @Column(name = "job_type", columnDefinition = "INT")
    @Comment("직종")
    private Integer jobType;

    @Column(name = "selected_food_id", columnDefinition = "VARCHAR(255)")
    @Comment("선호하는 음식 ID")
    private String selectedFoodId;

    @Column(name = "unselected_food_id", columnDefinition = "VARCHAR(255)")
    @Comment("선호하지 않는 음식 ID")
    private String unselectedFoodId;


}
