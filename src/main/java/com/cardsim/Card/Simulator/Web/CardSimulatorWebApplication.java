package com.cardsim.Card.Simulator.Web;

import com.cardsim.Card.Simulator.Web.controller.FirebaseUsage;
import com.cardsim.Card.Simulator.Web.Service.FirebaseService;
import com.cardsim.Card.Simulator.Web.controller.HomeController;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
//@ComponentScan(basePackageClasses = {HomeController.class, FirebaseUsage.class, FirebaseService.class, FirebaseApp.class})
public class CardSimulatorWebApplication {

	public static void main(String[] args) throws IOException, FirebaseAuthException {
		SpringApplication.run(CardSimulatorWebApplication.class, args);
//		Dotenv dotenv = Dotenv.configure()
//        .directory("")
//        .ignoreIfMalformed()
//        .ignoreIfMissing()
//        .load();
//		Dotenv dotenvLoad = Dotenv.load();
//		String firebaseAdmin = dotenvLoad.get("FIREBASE_ADMIN_SDK_FILENAME");
//		FileInputStream serviceAccount = new FileInputStream(firebaseAdmin);

//		FirebaseOptions options = new FirebaseOptions.Builder()
//				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
//				.build();
//		FirebaseApp app = FirebaseApp.initializeApp(optionss);
//		FirebaseService test = new FirebaseService(app);
//
//		FirebaseAuth auth = FirebaseAuth.getInstance(app);
//
//		UserManagement test1 = new UserManagement(auth);

	}

}
