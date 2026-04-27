package com.example.API_Gatway.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void sendRealTimeAlert(String title, String body) {
        try {
            // We send to a "surveillance" topic so all connected admin devices get it instantly
            Message message = Message.builder()
                    .setTopic("surveillance")
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🔥 [FIREBASE] Real-time Alert Sent: " + response);
        } catch (Exception e) {
            System.err.println("❌ [FIREBASE] Failed to send alert: " + e.getMessage());
        }
    }
}
