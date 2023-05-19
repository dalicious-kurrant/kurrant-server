package co.dalicious.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class FoodGroupDto {

    @Getter
    @Setter
    public static class Response {
        private String makers;
        private BigInteger id;
        private String name;
        private String groupNumbers;
        private String saladPercent;
        private String dinnerBoxPercent;
        private String proteinPercent;
        private String dietPercent;
        private String postpartumPercent;
        private String singleBowlPercent;
        private String conveniencePercent;

        @Builder

        public Response(String makers, BigInteger id, String name, String groupNumbers, String saladPercent, String dinnerBoxPercent, String proteinPercent, String dietPercent, String postpartumPercent, String singleBowlPercent, String conveniencePercent) {
            this.makers = makers;
            this.id = id;
            this.name = name;
            this.groupNumbers = groupNumbers;
            this.saladPercent = saladPercent;
            this.dinnerBoxPercent = dinnerBoxPercent;
            this.proteinPercent = proteinPercent;
            this.dietPercent = dietPercent;
            this.postpartumPercent = postpartumPercent;
            this.singleBowlPercent = singleBowlPercent;
            this.conveniencePercent = conveniencePercent;
        }
    }

    @Getter
    @Setter
    public static class Request {
        private String makers;
        private BigInteger id;
        private String name;
        private String groupNumbers;
    }

}
