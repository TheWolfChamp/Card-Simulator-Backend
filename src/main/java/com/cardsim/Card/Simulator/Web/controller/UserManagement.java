package com.cardsim.Card.Simulator.Web.Service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/users")
@RestController
public class UserManagement {

    private final UserManagementService userService;

    @Autowired
    public UserManagement(UserManagementService auth) {
        this.userService = auth;
    }

    /**
     * Creates a new user in the Firebase Auth system.
     * @param email Email address
     * @param password Password
     * @param displayName Display Name (also becomes the Unique Identifier)
     * @throws FirebaseAuthException
     */
    @PutMapping("/registerEmail")
    public void registerUserEmail(String email, String password, String displayName) throws FirebaseAuthException {
        this.userService.registerUserEmail(email,password,displayName);
    }

    /**
     * Prints all users' Unique Identifier
     * @throws FirebaseAuthException
     */
    @GetMapping("/all-users")
    public void getAllUsers() throws FirebaseAuthException {
        this.userService.getAllUsers();
    }
}
