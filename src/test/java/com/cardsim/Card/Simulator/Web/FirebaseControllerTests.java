package com.cardsim.Card.Simulator.Web;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseApp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.FileInputStream;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.cdimascio.dotenv.Dotenv;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"com.cardsim.Card.Simulator.Web.controller", "com.cardsim.Card.Simulator.Web.Service"})
public class FirebaseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Firestore firestore;

    @Autowired
    private FirebaseApp firebaseApp;

    @BeforeEach
    public void setup() throws IOException {
        // Any necessary setup can be done here
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testGetCardDetails() throws Exception {
        mockMvc.perform(get("/data/card-details")
                        .header("Accept", "application/json")
                        .param("expansionName", "Romance Dawn (OP 01)")
                        .param("cardName", "OP01-001"))
                .andExpect(status().isOk());
    }

//    @Test
//    @WithMockUser(username = "user", roles = {"USER"})
//    public void testGetCollectionDetails() throws Exception {
//        mockMvc.perform(get("/data/collection-details")
//                        .header("Accept", "application/json")
//                        .param("expansionName", "Romance Dawn (OP 01)"))
//                .andExpect(status().isOk());
//    }

    @Configuration
    static class TestConfig {

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
        public Firestore firestore(FirebaseApp firebaseApp) {
            return FirestoreClient.getFirestore(firebaseApp);
        }
    }
}