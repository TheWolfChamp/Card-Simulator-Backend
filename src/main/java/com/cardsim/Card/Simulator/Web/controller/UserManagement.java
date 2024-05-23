package com.cardsim.Card.Simulator.Web.controller;

import com.cardsim.Card.Simulator.Web.Service.UserManagementService;
import com.google.firebase.auth.FirebaseAuthException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RequestMapping("/users")
@RestController
public class UserManagement {

    private final UserManagementService userService;

    @Autowired
    public UserManagement(UserManagementService auth) {
        this.userService = auth;
    }

    /**
     *
     * @param registerRequest
     * @return
     * @throws FirebaseAuthException
     */
    @PutMapping("/registerEmail")
    public ResponseEntity<Map<String, Object>> registerUserEmail(@RequestBody Map<String, String> registerRequest)
            throws FirebaseAuthException {
        String email = registerRequest.get("email");
        String password = registerRequest.get("password");
        String displayName = registerRequest.get("displayName");

        Map<String, Object> response = this.userService.registerUserEmail(email, password, displayName);
        return ResponseEntity.ok(response);
    }

    /**
     * Prints all users' Unique Identifier
     *
     * @throws FirebaseAuthException
     */
    @GetMapping(value = "/all-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllUsers() throws FirebaseAuthException {
        return this.userService.getAllUsers();
    }
}
