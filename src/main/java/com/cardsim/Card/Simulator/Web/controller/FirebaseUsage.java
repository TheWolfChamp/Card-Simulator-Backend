package com.cardsim.Card.Simulator.Web.controller;
import com.cardsim.Card.Simulator.Web.Service.FirebaseService;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/data")
public class FirebaseUsage {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseUsage(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @PutMapping("/update")
    public void updateCardData(@RequestParam String collectionName, @RequestParam String cardName,
                               @RequestBody Map<String, Object> cardData) {
        this.firebaseService.updateCardData(collectionName, cardName, cardData);
    }

    @GetMapping("/image")
    public Blob getCardImageDetails(@RequestParam String expansionName, @RequestParam String cardName) {
        return this.firebaseService.getCardImageDetails(expansionName, cardName);
    }

    @GetMapping("/imageLink")
    public String getCardImageViewableLink(@RequestParam Blob cardDetails) {
        return this.firebaseService.getCardImageViewableLink(cardDetails);
    }

    @GetMapping(value = "/card-details", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> getCardDetails(@RequestParam String expansionName, @RequestParam String cardName)
            throws ExecutionException, InterruptedException {
        return this.firebaseService.getCardDetails(expansionName, cardName);
    }

    @GetMapping(value = "/collection-details", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Map<String, Object>> getCollectionDetails(@RequestParam String expansionName)
            throws ExecutionException, InterruptedException {
        return this.firebaseService.getCollectionDetails(expansionName);
    }

    @PutMapping("/upload-collection")
    public void uploadJsonToCollection(@RequestParam String collectionName, @RequestParam String jsonName) {
        this.firebaseService.uploadJsonToCollection(collectionName, jsonName);
    }
}