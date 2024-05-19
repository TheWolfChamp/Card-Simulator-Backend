package com.cardsim.Card.Simulator.Web;

import com.cardsim.Card.Simulator.Web.Service.FirebaseService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.FileInputStream;
import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class TestConfig {

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

    @Bean
    public FirebaseService firebaseService(FirebaseApp firebaseApp) throws IOException, FirebaseAuthException {
        return new FirebaseService(firebaseApp);
    }
}