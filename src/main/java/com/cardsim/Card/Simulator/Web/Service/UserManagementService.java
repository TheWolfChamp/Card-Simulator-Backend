package com.cardsim.Card.Simulator.Web.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Service
public class UserManagementService {
    FirebaseAuth authDatabase;
    FirebaseApp app;

    @Autowired
    public UserManagementService(FirebaseAuth auth) {
        this.authDatabase = auth;
    }

    /**
     * Verifies if a user is already in the database
     * @param email Email address for the user
     * @return boolean on whether user exists or not.
     * @throws FirebaseAuthException
     */
    public boolean verifyUserExists(String email) throws FirebaseAuthException {
        try {
            UserRecord userRecord = this.authDatabase.getUserByEmail(email);
            System.out.println("User exists with UID: " + userRecord.getUid());
            return true;
        } catch (FirebaseAuthException e) {
            if (e.getMessage().contains("No user record found")) {
                System.out.println("User does not exist with email: " + email);
            } else {
                // Handle other errors
                System.out.println(e.getErrorCode());
                System.out.println("Error: " + e.getMessage());
            }

            return false;
        }
    }

    /**
     * Creates a new user in the Firebase Auth system.
     * @param email Email address
     * @param password Password
     * @param displayName Display Name (also becomes the Unique Identifier)
     * @throws FirebaseAuthException
     */
    public void registerUserEmail(String email, String password, String displayName) throws FirebaseAuthException {
        // Check if username is already taken
        if(verifyUserExists(email)){
            System.out.println("User already exists with this email. Account cannot be created.");
        }
        else{
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(false)
                    .setPassword(password)
                    .setDisplayName(displayName)
                    .setUid(displayName)
                    .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
        }
    }

    /**
     * Prints all users' Unique Identifier
     * @throws FirebaseAuthException
     */
    public void getAllUsers() throws FirebaseAuthException {
        ListUsersPage page = this.authDatabase.listUsers(null);
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                // Process user
                System.out.println("User: " + user.getUid());
            }
            page = page.getNextPage(); // Get the next page of users
        }
    }
}
