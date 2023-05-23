package co.dalicious.domain.recommend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

public class FoodRecommendDto {
    @Getter
    @NoArgsConstructor
    public static class Response {
        private BigInteger id;
        private String groups;
        private List<TypeResponse> foodType;
        private List<GroupResponse> dailyFoodGroups;

        @Builder
        public Response(BigInteger id, String groups, List<TypeResponse> foodType, List<GroupResponse> dailyFoodGroups) {
            this.id = id;
            this.groups = groups;
            this.foodType = foodType;
            this.dailyFoodGroups = dailyFoodGroups;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TypeResponse {
        private Integer order;
        private String foodTypes;
        private String importances;

        @Builder
        public TypeResponse(Integer order, String foodTypes, String importances) {
            this.order = order;
            this.foodTypes = foodTypes;
            this.importances = importances;
        }
    }
    @Getter
    @NoArgsConstructor
    public static class GroupResponse {
        private Integer days;
        private String foodGroups;

        public GroupResponse(Integer days, String foodGroups) {
            this.days = days;
            this.foodGroups = foodGroups;
        }
    }
}
