package co.dalicious.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user__select_test_data")
public class UserSelectTestData {

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

    @Column(name="selected_food_id_list", columnDefinition = "VARCHAR(255)")
    @Comment("유저가 선택한 foodId")
    private String selectedFoodIds;

    @Column(name="unselected_food_id_list", columnDefinition = "VARCHAR(255)")
    @Comment("유저가 선택하지 않은 foodId")
    private String unselectedFoodIds;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_preference_id")
    @Comment("회원정보 ID")
    private UserPreference userPreference;

    @Builder
    public UserSelectTestData(User user, String selectedFoodIds, String unselectedFoodIds, UserPreference userPreference){
        this.user = user;
        this.selectedFoodIds = selectedFoodIds;
        this.unselectedFoodIds = unselectedFoodIds;
        this.userPreference = userPreference;
    }

}
