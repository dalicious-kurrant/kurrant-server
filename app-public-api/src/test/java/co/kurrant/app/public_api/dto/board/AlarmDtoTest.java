package co.kurrant.app.public_api.dto.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;


public class AlarmDtoTest {


    @Test
    public void alarmDtoTest() {
        //given
        BigInteger userId = BigInteger.valueOf(44);

        //when
        AlarmDto alarmDto = new AlarmDto();
        alarmDto.setId(BigInteger.valueOf(44));

        //then
        Assertions.assertEquals(alarmDto.getId(), userId);
    }




}
