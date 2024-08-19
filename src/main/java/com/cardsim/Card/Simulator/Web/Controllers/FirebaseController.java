package com.cardsim.Card.Simulator.Web.Controllers;
import com.cardsim.Card.Simulator.Web.Service.FirebaseService;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/data")
public class FirebaseController {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @PostMapping("/update-card")
    public void updateCardData(@RequestParam String series, @RequestParam String collectionName,
                               @RequestParam String cardName, @RequestBody Map<String, Object> cardData) {
        this.firebaseService.updateCardData(series, collectionName, cardName, cardData);
    }

    @PostMapping("/update-set")
    public void updateSetData(@RequestParam String series, @RequestParam String expansionName,
                              @RequestBody Map<String, Object> metadata) {
        this.firebaseService.updateSetData(series, expansionName, metadata);
    }

    @PostMapping("/set-card")
    public void setCardData(@RequestParam String series, @RequestParam String collectionName,
                               @RequestParam String cardName, @RequestBody Map<String, Object> cardData) {
        this.firebaseService.setCardData(series, collectionName, cardName, cardData);
    }

    @PostMapping("/set-set")
    public void setSetData(@RequestParam String series, @RequestParam String expansionName,
                              @RequestBody Map<String, Object> metadata) {
        this.firebaseService.setSetData(series, expansionName, metadata);
    }

    @GetMapping("/image")
    public Blob getCardImageDetails(@RequestParam String expansionName, @RequestParam String cardName) {
        return this.firebaseService.getCardImageDetails(expansionName, cardName);
    }

    @GetMapping("/image-link")
    public String getCardImageViewableLink(@RequestParam Blob cardDetails) {
        return this.firebaseService.getCardImageViewableLink(cardDetails);
    }

    @GetMapping(value = "/card-details", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Object> getCardDetails(@RequestParam String series,
                                                  @RequestParam String expansionName,
                                                  @RequestParam String cardName)
            throws ExecutionException, InterruptedException {
        return this.firebaseService.getCardDetails(series, expansionName, cardName);
    }

    @GetMapping(value = "/collection-details", produces = MediaType.APPLICATION_JSON_VALUE)
    public HashMap<String, Map<String, Object>> getCollectionDetails(@RequestParam String series,
                                                                     @RequestParam String expansionName)
            throws ExecutionException, InterruptedException {
        return this.firebaseService.getCollectionDetails(series, expansionName);
    }

    @PostMapping("/upload-collection")
    public ResponseEntity<Map<String, String>> uploadJsonToCollection(@RequestParam String series,
                                                                      @RequestParam String packName,
                                                                      @RequestBody String jsonBody) {
        try {
            this.firebaseService.uploadJsonToCollection(series, packName, jsonBody);
            return ResponseEntity.ok(Map.of("status", "Successfully uploaded collection!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/update-image-links")
    public ResponseEntity<Map<String, String>> updateImageLinks(@RequestParam String databaseCollectionName,
                                                                     @RequestParam String series,
                                                                     @RequestParam String storageBucketName) {
        try {
            this.firebaseService.updateImageLinks(databaseCollectionName, series, storageBucketName);
            return ResponseEntity.ok(Map.of("status", "Successfully updated image links!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}