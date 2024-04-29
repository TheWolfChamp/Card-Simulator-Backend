package com.cardsim.Card.Simulator.Web;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import io.github.cdimascio.dotenv.Dotenv;


// Path to your 'google-services.json' file
public class FirebaseUsage {
    private final Firestore database;
    public FirebaseUsage() throws IOException {
        Dotenv dotenv = Dotenv.load();
        String firebaseAdmin = dotenv.get("FIREBASE_ADMIN_SDK_FILENAME");
        FileInputStream serviceAccount = new FileInputStream(firebaseAdmin);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);


        this.database = FirestoreClient.getFirestore();
    }

    public void updateCardData(String collectionName, String cardName, Map<String, Object> cardData){
        this.database.collection(collectionName).document(cardName).set(cardData);
    }

    /**
     * Takes in the name of a collection, and a JSON name and filepath.
     * Uploads everything in that JSON to the database.
     * @param collectionName
     * @param jsonName
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
