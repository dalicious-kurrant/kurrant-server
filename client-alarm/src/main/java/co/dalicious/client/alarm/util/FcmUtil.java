package co.dalicious.client.alarm.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
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
