package com.cardsim.Card.Simulator.Web;

import com.cardsim.Card.Simulator.Web.Authorization.UserManagement;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import org.apache.catalina.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileInputStream;
import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class CardSimulatorWebApplication {

	public static void main(String[] args) throws IOException, FirebaseAuthException {
		Dotenv dotenv = Dotenv.configure()
        .directory("")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load();
		SpringApplication.run(CardSimulatorWebApplication.class, args);
		Dotenv dotenvLoad = Dotenv.load();
		String firebaseAdmin = dotenvLoad.get("FIREBASE_ADMIN_SDK_FILENAME");
		FileInputStream serviceAccount = new FileInputStream(firebaseAdmin);

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
		FirebaseApp app = FirebaseApp.initializeApp(options);
		FirebaseUsage test = new FirebaseUsage(app);

		FirebaseAuth auth = FirebaseAuth.getInstance(app);

		UserManagement test1 = new UserManagement(auth);


	}

}
