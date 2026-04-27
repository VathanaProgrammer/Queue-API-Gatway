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
                String fileName = "queue-project-9c331-firebase-adminsdk-fbsvc-1e36769c33.json";
                InputStream serviceAccount;

                try {
                    serviceAccount = new ClassPathResource(fileName).getInputStream();
                } catch (IOException e) {
                    System.out.println("⚠️ [FIREBASE] ClassPath load failed, trying relative project path...");
                    // This works on both Windows and Linux (AWS)
                    serviceAccount = new java.io.FileInputStream("src/main/resources/" + fileName);
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("🔥 [FIREBASE] Successfully Initialized!");
            }
        } catch (Exception e) {
            System.err.println("❌ [FIREBASE ERROR] CRITICAL FAILURE: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
