package co.kurrant.app.client_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class MemberMemoSaveDto {

    private String memo;
    private BigInteger userId;

}
