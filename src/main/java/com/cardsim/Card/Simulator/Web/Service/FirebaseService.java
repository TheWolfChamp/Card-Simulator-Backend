package com.cardsim.Card.Simulator.Web.Service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
     * @param series Name of series the card is from
     * @param expansionName Name of expansion to store the information in
     * @param cardName What you would like to label the card as
     * @param cardData Information on the card
     */
    public void updateCardData(
            String series, String expansionName, String cardName, Map<String, Object> cardData){
        this.database.collection(series).document(expansionName).collection("Cards")
                .document(cardName).set(cardData);
    }

    /**
     * Updates the information on a particular set
     * @param series Name of series the card is from
     * @param expansionName Name of expansion to store the information in
     * @param metadata Information on the set
     */
    public void updateSetData(
            String series, String expansionName, Map<String, Object> metadata){
        this.database.collection(series).document(expansionName).set(metadata);
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
        if(expansionName.contains("Pokemon")){
            return this.storage.get(expansionName+"/"+cardName);
        }
        else if(expansionName.contains("JP")){
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
     * @param series Name of series
     * @param expansionName Name of expansion
     * @param cardName ID of the card
     * @return Card data
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public HashMap<String, Object> getCardDetails(String series, String expansionName, String cardName)
            throws ExecutionException, InterruptedException {

        return (HashMap<String, Object>) this.database.collection(series)
                .document(expansionName).collection("Cards")
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
    public HashMap<String, Map<String, Object>> getCollectionDetails(String series, String expansionName)
            throws ExecutionException, InterruptedException {

        ApiFuture<QuerySnapshot> querySnapshot = this.database.collection(series).document(expansionName)
                .collection("Cards")
                .get();
        HashMap<String, Map<String, Object>> documents = new HashMap<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            documents.put(doc.getId(), doc.getData());
        }

        return documents;
    }

    /**
     * Takes in the name of a collection, and a JSON name and filepath.
     * Uploads everything in that JSON to the database.
     * @param series Name of JSON file to upload.
     * @param packName Name of collection to store the information in.
     * @param jsonBody Body containing JSON of the object you want to upload.
     */
    public void uploadJsonToCollection(String series, String packName, String jsonBody){
        try {
            CollectionReference seriesRef = this.database.collection(series);
            DocumentReference packDocRef = seriesRef.document(packName);
            CollectionReference packCollectionRef = packDocRef.collection("Cards");

            // Parse JSON data
            JsonElement jsonElement = JsonParser.parseString(jsonBody);
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Iterate through JSON array and send data to Firestore
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();

                String cardId = jsonObject.has("Id") ? jsonObject.get("Id").getAsString() : jsonObject.get("id").getAsString();;
                Map<String, Object> cardData = new Gson().fromJson(jsonObject, Map.class);

                this.database.runTransaction(transaction -> {
                    DocumentReference docRef = packCollectionRef.document(cardId);
                    ApiFuture<DocumentSnapshot> future = transaction.get(docRef);

                    DocumentSnapshot document = future.get();
                    if (document.exists()) {
                        // Document already exists, need to update ID
                        int suffix = 1;
                        String newCardId = cardId + "_p" + suffix;

                        // Keep incrementing suffix until a unique ID is found
                        while (transaction.get(packCollectionRef.document(newCardId)).get().exists()) {
                            suffix++;
                            newCardId = cardId + "_p" + suffix;
                        }
                        docRef = packCollectionRef.document(newCardId);

                        // Update the document with the new ID
                        transaction.set(docRef, cardData);
                    } else {
                        // Document does not exist, create a new one with the original ID
                        transaction.set(docRef, cardData);
                    }
                    return null;
                }).get(); // Wait for the transaction to complete
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates image links of card objects in a particular database from a specific series
     * @param databaseCollectionName Name of Card Database (Romance-Dawn-OP-01)
     * @param series Name of Card Game (One Piece)
     * @param storageBucketName Name of Storage Bucket (OP 01 Card Images)
     */
    public void updateImageLinks(String databaseCollectionName, String series, String storageBucketName) {
        CollectionReference seriesRef = this.database.collection(series);
        DocumentReference packDocRef = seriesRef.document(databaseCollectionName);
        CollectionReference packCollectionRef = packDocRef.collection("Cards");
        try {
            ApiFuture<QuerySnapshot> querySnapshot = packCollectionRef.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                String docId = document.getId();

                // Check if the file exists in Cloud Storage
                Blob blob = getCardImageDetails(storageBucketName, docId);
                if (blob != null && blob.exists()) {
                    // File exists, retrieve it
                    String urlLink = getCardImageViewableLink(blob);
                    DocumentReference docRef = packCollectionRef.document(docId);

                    Map<String, Object> deleteUpdate = new HashMap<>();
                    deleteUpdate.put("images", FieldValue.delete());
                    docRef.update(deleteUpdate).get();

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("Image-Link", urlLink);
                    docRef.update(updates).get();

                    // Process the content as needed
                    System.out.println("Retrieved file for ID: " + docId);
                } else {
                    System.out.println("No file found for ID: " + docId);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
