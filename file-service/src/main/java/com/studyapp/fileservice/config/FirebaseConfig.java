package com.studyapp.fileservice.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${app.firebase.bucketName}")
    private String bucketName;

    @Value("${app.firebase.configPath}")
    private String firebaseConfigPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Kiểm tra nếu FirebaseApp đã được khởi tạo
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream(firebaseConfigPath)) {

                if (serviceAccount == null) {
                    log.error("Service account JSON file not found: {}", firebaseConfigPath);
                    throw new IOException("Service account JSON file not found: " + firebaseConfigPath);
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setStorageBucket(String.format("%s.appspot.com", bucketName))
                        .build();

                // Khởi tạo FirebaseApp nếu chưa có
                return FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                log.error("Failed to initialize FirebaseApp: {}", e.getMessage());
                throw e;
            }
        } else {
            // Nếu đã có FirebaseApp, trả về FirebaseApp hiện tại
            return FirebaseApp.getInstance();
        }
    }
}
