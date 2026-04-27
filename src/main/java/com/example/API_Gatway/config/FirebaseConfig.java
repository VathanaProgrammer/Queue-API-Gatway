package com.example.API_Gatway.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = new ClassPathResource("queue-project-9c331-firebase-adminsdk-fbsvc-1e36769c33.json").getInputStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("🔥 [FIREBASE] Successfully Initialized using: queue-project-9c331-firebase-adminsdk-fbsvc-1e36769c33.json");
            }
        } catch (Exception e) {
            System.err.println("❌ [FIREBASE ERROR] Could not load service account JSON: " + e.getMessage());
        }
    }
}
