package com.cardsim.Card.Simulator.Web.Service;


import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
@Service
// Path to your 'google-services.json' file
public class FirebaseService {
    private final Firestore database;
    private final FirebaseApp app;

    protected final FirebaseAuth auth;
    private final Bucket storage;

    @Autowired
    public FirebaseService(FirebaseApp initialApp) throws IOException, FirebaseAuthException {
        this.app = initialApp;
        this.database = FirestoreClient.getFirestore();
        this.auth = FirebaseAuth.getInstance();
        this.storage = StorageClient.getInstance(this.app).bucket("card-simulator.appspot.com");
    }

    /**
     * Uploads card data to firebase database.
     * @param collectionName Name of collection to store the information in
     * @param cardName What you would like to label the card as
     * @param cardData Information on the card
     */
    public void updateCardData(String collectionName, String cardName, Map<String, Object> cardData){
        this.database.collection(collectionName).document(cardName).set(cardData);
    }

    /**
     * Gets the blob containing the information for any given card
     * Example Run(s):
     * getCardImageDetails("OP-01 Card Images", "OP01-001")
     * @param expansionName Name of the expansion pack in storage
     * @param cardName ID of the card you wish to get the data for
     * @return Blob containing data about the card.
     */
    public Blob getCardImageDetails(String expansionName, String cardName){
        if(expansionName.contains("JP")){
            return this.storage.get(expansionName+"/"+cardName+"_JP.png");
        }
        else{
            return this.storage.get(expansionName+"/"+cardName+"_EN.png");
        }
    }

    /**
     * Gets a viewable link for the card image
     * @param cardDetails Blob containing all the card details
     * @return Image link to view the image
     */
    public String getCardImageViewableLink(Blob cardDetails){
        // Makes duration for 100 years
        long duration = 100L * 365 * 24 * 60 * 60;
        return cardDetails.signUrl(duration, TimeUnit.SECONDS).toString();

    }

    /**
     * Returns data about a card
     * Example Run: getCardDetails("Romance Dawn (OP 01)", "OP01-001")
     * @param expansionName Name of expansion
     * @param cardName ID of the card
     * @return Card data
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public HashMap<String, Object> getCardDetails(String expansionName, String cardName)
            throws ExecutionException, InterruptedException {

        return (HashMap<String, Object>) this.database.collection(expansionName)
                .document(cardName).get().get().getData();
    }

    /**
     * Returns data about all cards in a collection
     * Example run: getCollectionDetails("Romance Dawn (OP 01)")
     * @param expansionName Name of expansion
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public HashMap<String, Map<String, Object>> getCollectionDetails(String expansionName)
            throws ExecutionException, InterruptedException {

        Iterator<DocumentReference> iterator = this.database.collection(expansionName)
                .listDocuments().iterator();
        HashMap<String, Map<String, Object>> documentList = new HashMap<>();
        while (iterator.hasNext()) {
            DocumentSnapshot item = iterator.next().get().get();
            documentList.put(item.getId(), item.getData());
        }
        return documentList;

    }

    /**
     * Takes in the name of a collection, and a JSON name and filepath.
     * Uploads everything in that JSON to the database.
     * @param collectionName Name of collection to store the information in.
     * @param jsonName Name of JSON file to upload.
     */
    public void uploadJsonToCollection(String collectionName, String jsonName){
        try {
            CollectionReference packRef = this.database.collection(collectionName);

            String jsonFilePath = jsonName;
            String jsonData = new String(Files.readAllBytes(Paths.get(jsonFilePath)));

            // Parse JSON data
            JsonElement jsonElement = JsonParser.parseString(jsonData);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Iterate through JSON array and send data to Firestore
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();

                String cardId= jsonObject.get("Id").getAsString();
                Map<String, Object> cardData = new Gson().fromJson(jsonObject, Map.class);

                this.database.runTransaction(transaction -> {
                    DocumentReference docRef = packRef.document(cardId);
                    ApiFuture<DocumentSnapshot> future = transaction.get(docRef);

                    DocumentSnapshot document = future.get();
                    if (document.exists()) {
                        // Document already exists, need to update ID
                        int suffix = 1;
                        String newCardId = cardId + "_p" + suffix;

                        // Keep incrementing suffix until a unique ID is found
                        while (transaction.get(packRef.document(newCardId)).get().exists()) {
                            suffix++;
                            newCardId = cardId + "_p" + suffix;
                        }
                        docRef = packRef.document(newCardId);

                        // Update the document with the new ID
                        transaction.set(docRef, cardData);
                    } else {
                        // Document does not exist, create a new one with the original ID
                        transaction.set(docRef, cardData);
                    }
                    return null;
                }).get(); // Wait for the transaction to complete
            }
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
