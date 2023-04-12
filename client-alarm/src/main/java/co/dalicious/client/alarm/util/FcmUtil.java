package co.dalicious.client.alarm.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class FcmUtil {

    private static final String FIREBASE_CONFIG_PATH = "firebase.json";

    @PostConstruct
    public void initialize() {

            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options, "dalicious-v1");
                    System.out.println("FCM 초기화!");
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
    }

}
