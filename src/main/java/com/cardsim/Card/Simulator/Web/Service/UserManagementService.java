package com.cardsim.Card.Simulator.Web.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public boolean verifyUserExistsEmail(String email) throws FirebaseAuthException {
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
     * Verifies user exists with a Unique Identifier
     * @param uid
     * @return
     */
    public boolean verifyUserExistsUID(String uid) {
        try {
            UserRecord userRecord = this.authDatabase.getUser(uid);
            System.out.println("User exists with UID: " + userRecord.getUid());
            return true;
        } catch (FirebaseAuthException e) {
            if (e.getMessage().contains("No user record found")) {
                System.out.println("User does not exist with uid: " + uid);
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
     *
     * @param email       Email address
     * @param password    Password
     * @param displayName Display Name (also becomes the Unique Identifier)
     * @return
     * @throws FirebaseAuthException
     */
    public Map<String, Object> registerUserEmail(String email, String password, String displayName) throws FirebaseAuthException {
        Map<String, Object> response = new HashMap<>();

        // Check if username is already taken
        if (verifyUserExistsEmail(email)) {
            response.put("success", false);
            response.put("message", "User already exists with this email. Account cannot be created.");
        }
        else if(verifyUserExistsEmail(displayName)){
            response.put("success", false);
            response.put("message", "User already exists with this Display Name. Account cannot be created.");
        }else {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(false)
                    .setPassword(password)
                    .setDisplayName(displayName)
                    .setUid(displayName)
                    .setDisabled(false);
            try {
                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
                response.put("success", true);
                response.put("message", "Successfully created new user: " + userRecord.getUid());
            } catch (FirebaseAuthException e) {
                response.put("success", false);
                response.put("message", "Error creating user: " + e.getMessage());
            }
        }
        return response;

    }

    /**
     * Prints all users' Unique Identifier
     *
     * @return
     * @throws FirebaseAuthException
     */
    public List<Map<String, Object>> getAllUsers() throws FirebaseAuthException {
        ListUsersPage page = this.authDatabase.listUsers(null);
        List<Map<String, Object>> users = new ArrayList<>();
        while (page != null) {
            for (ExportedUserRecord user : page.getValues()) {
                // Process user
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("uid", user.getUid());
                userMap.put("email", user.getEmail());
                userMap.put("displayName", user.getDisplayName());
                System.out.println("User: " + user.getUid());
                users.add(userMap);
            }
            page = page.getNextPage(); // Get the next page of users
        }
        return users;
    }
}
