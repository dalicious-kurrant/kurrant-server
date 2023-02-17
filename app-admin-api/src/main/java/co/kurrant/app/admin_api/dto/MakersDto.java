package co.kurrant.app.admin_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

public class MakersDto {
    @Setter
    @Getter
    public static class Makers {
        private BigInteger makersId;
        private String makersName;
    }
}
