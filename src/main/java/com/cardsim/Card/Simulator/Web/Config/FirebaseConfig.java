package com.cardsim.Card.Simulator.Web.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        Dotenv dotenv = Dotenv.configure()
                .directory("")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        Dotenv dotenvLoad = Dotenv.load();
        String firebaseAdmin = dotenvLoad.get("FIREBASE_ADMIN_SDK_FILENAME");
        FileInputStream serviceAccount = new FileInputStream(firebaseAdmin);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}