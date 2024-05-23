package com.cardsim.Card.Simulator.Web.controller;

import com.cardsim.Card.Simulator.Web.Service.AuthService;
import com.cardsim.Card.Simulator.Web.Service.UserManagementService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService auth) {
        this.authService = auth;
    }

    /**
     *
     * @param loginRequest Request with Email and Password in the body
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        try {
            Map<String, Object> response = this.authService.loginWithEmailPassword(email, password);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error during login"));
        }
    }

    @PostMapping("/googleSignIn")
    public ResponseEntity<Map<String, Object>> googleSignIn(@RequestBody Map<String, String> requestBody) {
        String idToken = requestBody.get("idToken");

        try {
            System.out.println("Test");
            String customToken = authService.googleSignIn(idToken);
            return ResponseEntity.ok(Map.of("customToken", customToken));
        } catch (FirebaseAuthException | IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error: " + e.getMessage()));
        }
    }

}
