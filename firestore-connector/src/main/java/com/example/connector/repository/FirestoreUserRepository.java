package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.spotify.futures.ApiFuturesExtra;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
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

    public Mono<List<User>> findUsers(Map<String, String> queryParams) {

        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        Query query = usersCollectionReference;

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }

        ApiFuture<QuerySnapshot> querySnapshotApiFuture =
                Optional.of(query)
                        .map(Query::get)
                        .orElse(usersCollectionReference.get());

        CompletableFuture<QuerySnapshot> querySnapshotFuture =
                ApiFuturesExtra.toCompletableFuture(querySnapshotApiFuture);

        return Mono.fromFuture(querySnapshotFuture).map(qs->qs.toObjects(User.class));
    }

    public void save(User user) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.add(user);
    }

    public void update(User user) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.document(user.getId()).set(user);
    }

    public void delete(String id) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.document(id).delete();
    }

}
