package co.dalicious.client.alarm.util;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

@Service
@PropertySource("classpath:application-alimtalk.properties")
public class FcmUtil {

    @Value("${fcm.key.type}")
    private String type;
    @Value("${fcm.key.project_id}")
    private String projectId;
    @Value("${fcm.key.private_key_id}")
    private String privateKeyId;
    @Value("${fcm.key.private_key}")
    private String privateKey;
    @Value("${fcm.key.client_email}")
    private String clientEmail;
    @Value("${fcm.key.client_id}")
    private String clientId;


    @PostConstruct
    public void initialize() {

            try {
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(
                                String.format(
                                        "{\"type\":\"%s\",\"project_id\":\"%s\",\"private_key_id\":\"%s\",\"private_key\":\"%s\",\"client_email\":\"%s\",\"client_id\":\"%s\"}",
                                        type, projectId, privateKeyId, privateKey, clientEmail, clientId
                                ).getBytes()
                        )
                );
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
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
