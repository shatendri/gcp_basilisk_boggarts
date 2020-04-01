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
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private String usersCollectionName;
    private final Firestore firestore;

    public UserRepositoryImpl(@Value("${firestore.users-collection}") String usersCollectionName,
                              Firestore firestore) {
        this.usersCollectionName = usersCollectionName;
        this.firestore = firestore;
    }

    @Override
    public Mono<List<User>> findAll(Map<String, String> queryParams) {

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

        return Mono.fromFuture(querySnapshotFuture).map(qs -> qs.toObjects(User.class));
    }

    @Override
    public void save(User user) {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.add(user);
    }

    @Override
    public void update(User user) throws ExecutionException, InterruptedException {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.whereEqualTo("id", user.getId()).get().get().getDocuments().stream()
                .map(DocumentSnapshot::getReference)
                .findAny()
                .ifPresent(dr->dr.set(user));
    }

    @Override
    public void delete(String id) throws ExecutionException, InterruptedException {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.whereEqualTo("id", id).get().get().getDocuments().stream()
                .map(DocumentSnapshot::getReference)
                .findAny()
                .ifPresent(DocumentReference::delete);
    }

}
