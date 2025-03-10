package com.spring.nuqta.notifications.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            initializeFirebaseApp();
        }
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            return initializeFirebaseApp();
        } else {
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    /**
     * Initializes the Firebase app using the service account file.
     * Reused by both `initFirebase` and `firebaseApp` to avoid duplication.
     */
    private FirebaseApp initializeFirebaseApp() throws IOException {
        InputStream serviceAccount = loadFirebaseServiceAccount();
        if (serviceAccount == null) {
            throw new IOException("firebase.file.notFound");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    /**
     * Extracted method to load Firebase service account file.
     * Allows better testability by mocking.
     */
    protected InputStream loadFirebaseServiceAccount() {
        return getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
    }
}