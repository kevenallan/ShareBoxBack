package br.com.sharebox.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.sharebox.config}")
    private String firebaseShareboxConfig;
	
	@Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream(firebaseShareboxConfig);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setDatabaseUrl("https://<seu-projeto>.firebaseio.com")  // Para Realtime Database
                //.setDatabaseUrl("https://<seu-projeto>.firestore.googleapis.com")  // Para Firestore
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
