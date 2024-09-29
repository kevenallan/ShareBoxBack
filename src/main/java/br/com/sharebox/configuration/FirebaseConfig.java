package br.com.sharebox.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.sharebox.config}")
    private String firebaseShareboxConfig;
	
	@Bean
    public FirebaseApp initializeFirebase() throws IOException {
		 InputStream firebaseConfigStream = new ByteArrayInputStream(firebaseShareboxConfig.getBytes(StandardCharsets.UTF_8));

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseConfigStream))
//                .setDatabaseUrl("https://<seu-projeto>.firebaseio.com")  // Para Realtime Database
                //.setDatabaseUrl("https://<seu-projeto>.firestore.googleapis.com")  // Para Firestore
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
