package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

public class FoodGroupDto {

    @Getter
    public static class Response {
        private String makers;
        private BigInteger id;
        private String name;
        private String groupNumbers;
        private String saladPercent;
        private String bentoPercent;
        private String proteinPercent;
        private String dietPercent;
        private String postpartumPercent;
        private String sandwichPercent;
        private String conveniencePercent;

        @Builder
        public Response(String makers, BigInteger id, String name, String groupNumbers, String saladPercent, String bentoPercent, String proteinPercent, String dietPercent, String postpartumPercent, String sandwichPercent, String conveniencePercent) {
            this.makers = makers;
            this.id = id;
            this.name = name;
            this.groupNumbers = groupNumbers;
            this.saladPercent = saladPercent;
            this.bentoPercent = bentoPercent;
            this.proteinPercent = proteinPercent;
            this.dietPercent = dietPercent;
            this.postpartumPercent = postpartumPercent;
            this.sandwichPercent = sandwichPercent;
            this.conveniencePercent = conveniencePercent;
        }
    }
}
