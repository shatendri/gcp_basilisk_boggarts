package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.spotify.futures.ApiFuturesExtra;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class FirestoreUserRepository {

    @Value("${firestore.user-collection}")
    private String usersCollectionName;

    private final Firestore firestore;

    public FirestoreUserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Mono<QuerySnapshot> findUsers(Map<String, String> queryParams) {

        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        Optional<Map.Entry<String, String>> first = queryParams.entrySet().stream().findFirst();

        Query query = null;
        if (first.isPresent()) {
            query = usersCollectionReference.whereEqualTo(first.get().getKey(), first.get().getValue());
            queryParams.remove(first.get().getKey());
        }

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }

        ApiFuture<QuerySnapshot> querySnapshotApiFuture =
                Optional.ofNullable(query)
                        .map(Query::get)
                        .orElse(usersCollectionReference.get());

        CompletableFuture<QuerySnapshot> querySnapshotFuture =
                ApiFuturesExtra.toCompletableFuture(querySnapshotApiFuture);

        return Mono.fromFuture(querySnapshotFuture);
    }

    public void save(User user) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        DocumentReference documentReference = usersCollectionReference.document();
        user.setId(documentReference.getId());
        documentReference.set(user);
    }

    public void delete(User user) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.document(user.getId()).delete();
    }

}
