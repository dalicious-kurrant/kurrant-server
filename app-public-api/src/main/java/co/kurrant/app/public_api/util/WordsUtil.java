package co.kurrant.app.public_api.util;

import exception.CustomException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WordsUtil {
    public static void isContainingSwearWords(String word) {
        InputStream inputStream = WordsUtil.class.getClassLoader().getResourceAsStream("nickname/adjective.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        List<String> swearWords = reader.lines().toList();

        Set<String> swearSet = new HashSet<>(swearWords);

        for (String swear : swearSet) {
            if (word.contains(swear)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "CE400023", "[" + swear + "] 은 닉네임에 포함될 수 없는 단어입니다.");
            }
        }
    }
}
