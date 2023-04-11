package co.dalicious.domain.user.entity;

import co.dalicious.domain.user.converter.BirthPlaceConverter;
import co.dalicious.domain.user.converter.CountryConverter;
import co.dalicious.domain.user.converter.JobTypeConverter;
import co.dalicious.domain.user.entity.enums.BirthPlace;
import co.dalicious.domain.user.entity.enums.Country;
import co.dalicious.domain.user.entity.enums.JobType;
import co.dalicious.system.converter.FoodTagsConverter;
import co.dalicious.system.enums.FoodTag;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.List;

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

    @Convert(converter = FoodTagsConverter.class)
    @Column(name = "favorite_country_food", columnDefinition = "VARCHAR(255)")
    @Comment("좋아하는 나라 음식")
    private List<FoodTag> favoriteCountryFood;

    @Convert(converter = FoodTagsConverter.class)
    @Column(name = "allergy_info", columnDefinition = "VARCHAR(255)")
    @Comment("알러지 정보")
    private List<FoodTag> allergyInfo;

    @Column(name = "allergy_info_etc", columnDefinition = "VARCHAR(255)")
    @Comment("알러지 정보")
    private String allergyInfoEtc;

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

    @Column(name = "protein_drink_frequency", columnDefinition = "INT")
    @Comment("프로틴 드링크 주간 섭취량")
    private Integer proteinDrinkFrequency;

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

    @Convert(converter = CountryConverter.class)
    @Column(name = "country", columnDefinition = "INT")
    @Comment("국가")
    private Country country;

    @Convert(converter = BirthPlaceConverter.class)
    @Column(name = "birth_place", columnDefinition = "INT")
    @Comment("출생지")
    private BirthPlace birthPlace;


    @Convert(converter = JobTypeConverter.class)
    @Column(name = "job_type", columnDefinition = "INT")
    @Comment("직종")
    private JobType jobType;

    @Column(name = "selected_food_id", columnDefinition = "VARCHAR(255)")
    @Comment("선호하는 음식 ID")
    private String selectedFoodId;

    @Column(name = "unselected_food_id", columnDefinition = "VARCHAR(255)")
    @Comment("선호하지 않는 음식 ID")
    private String unselectedFoodId;

    @Builder
    public UserPreference(User user,  Integer breakfastCount, Integer midnightSnackCount, Integer exerciseCount, Integer drinkCount,
                   List<FoodTag> favoriteCountryFood, List<FoodTag> allergyInfo, String allergyInfoEtc, Boolean isBegan, Integer beganLevel, Boolean isProtein,
                   Integer proteinScoop, Integer proteinBarFrequency, Integer proteinDrinkFrequency, String birthYear, String birthMonth,
                   String birthDay, Integer gender, Country country,  BirthPlace birthPlace, JobType jobType,
                   String selectedFoodId, String unselectedFoodId){
        this.user = user;
        this.breakfastCount = breakfastCount;
        this.midnightSnackCount = midnightSnackCount;
        this.exerciseCount = exerciseCount;
        this.drinkCount = drinkCount;
        this.favoriteCountryFood = favoriteCountryFood;
        this.allergyInfo = allergyInfo;
        this.allergyInfoEtc = allergyInfoEtc;
        this.isBegan = isBegan;
        this.beganLevel = beganLevel;
        this.isProtein = isProtein;
        this. proteinScoop = proteinScoop;
        this.proteinBarFrequency = proteinBarFrequency;
        this.proteinDrinkFrequency = proteinDrinkFrequency;
        this.birthYear = birthYear;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.gender = gender;
        this.country = country;
        this.birthPlace = birthPlace;
        this.jobType = jobType;
        this.selectedFoodId = selectedFoodId;
        this.unselectedFoodId = unselectedFoodId;

    }

}
