package com.example.connector.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.spotify.futures.ApiFuturesExtra;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Repository
public class FirestoreUserRepository {

    private static final String USERS_COLLECTION_NAME = "users";

    private final Firestore firestore;

    public FirestoreUserRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    public Mono<QuerySnapshot> findUsers(Map<String, String> queryParams) {

        CollectionReference usersCollectionReference = firestore.collection(USERS_COLLECTION_NAME);
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

        CompletableFuture<QuerySnapshot> documentSnapshotListenableFuture =
                ApiFuturesExtra.toCompletableFuture(querySnapshotApiFuture);

        return Mono.fromFuture(documentSnapshotListenableFuture);
    }
}
