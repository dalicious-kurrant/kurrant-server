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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WordsUtil {
    public static void isContainingSwearWords(String word) {
        InputStream inputStream = WordsUtil.class.getClassLoader().getResourceAsStream("fwords/fword_list.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        List<String> swearWords = reader.lines().toList();

        Set<String> swearSet = new HashSet<>(swearWords);

        for (String swear : swearSet) {
            if (word.contains(swear)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "CE400023", "[" + swear + "] 은 닉네임에 포함될 수 없는 단어입니다.");
            }
        }
    }

    public static String isContainingSwearWordsInContent(String content) {
        InputStream inputStream = WordsUtil.class.getClassLoader().getResourceAsStream("fwords/fword_list.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        Set<String> swearWords = reader.lines().collect(Collectors.toSet());

        StringBuilder filteredContent = new StringBuilder();
        for (String word : content.split("\\s+")) {
            if (swearWords.contains(word)) {
                filteredContent.append("*".repeat(word.length()));
            } else {
                filteredContent.append(word);
            }
            filteredContent.append(" ");
        }

        // remove the trailing space and return the result
        return filteredContent.toString().trim();
    }
}
