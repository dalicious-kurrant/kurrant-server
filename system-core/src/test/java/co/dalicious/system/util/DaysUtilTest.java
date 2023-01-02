package co.dalicious.system.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DaysUtilTest {

    @Test
    @DisplayName("날짜 배열 -> DB 데이터 변경")
    void serviceDaysToDbData() {
        // given
        List<Integer> integerDays = Arrays.asList(5, 4, 3, 2, 1);
        // when
        String dbData = DaysUtil.serviceDaysToDbData(integerDays);
        // then
        assertEquals(dbData, "1, 2, 3, 4, 5");
    }

    @Test
    @DisplayName("DB 데이터 -> 날짜 String 변경")
    void serviceDaysToString() {
        // given
        String dbData = "0, 2, 4";
        String dbData2 = "1, 3";
        // when
        String str = DaysUtil.serviceDaysToString(dbData);
        String str2 = DaysUtil.serviceDaysToString(dbData2);
        // then
        assertEquals("월, 수, 금", str);
        assertEquals("화, 목", str2);
    }
}