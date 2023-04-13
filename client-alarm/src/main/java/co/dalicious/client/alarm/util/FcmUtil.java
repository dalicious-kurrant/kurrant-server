package co.dalicious.client.alarm.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@PropertySource("classpath:application-alimtalk.properties")
public class FcmUtil {

    @Value("${fcm.key.path}")
    private String FIREBASE_CONFIG_PATH;

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
                throw new RuntimeException(e.getMessage());
            }
    }

}
