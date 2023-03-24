package co.kurrant.app.admin_api.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Service
public class FcmUtil {

    private static final String FIREBASE_CONFIG_PATH = "firebase.json";

    @PostConstruct
    public void initialize() {

            try {
                FirebaseOptions options = new FirebaseOptions.Builder()
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
