package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.spotify.futures.ApiFuturesExtra;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryImpl implements UserRepository {

  private final Firestore firestore;
  private final String usersCollectionName;

  public UserRepositoryImpl(
      final Firestore firestore,
      @Value("${firestore.users-collection-name}") final String usersCollectionName
  ) {
    this.firestore = firestore;
    this.usersCollectionName = usersCollectionName;
  }

  @Override
  public Mono<QuerySnapshot> findAll(Map<String, String> queryParams) {

    CollectionReference usersCollectionReference =
        firestore.collection(usersCollectionName);
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

    return Mono.fromFuture(querySnapshotFuture);
  }

  @Override
  public QuerySnapshot save(User user) {
    CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
    DocumentReference documentReference = usersCollectionReference.document();
    user.setId(documentReference.getId());
    documentReference.set(user);

    // TODO RESTful APIs should always return created entity
    // See: https://hackernoon.com/restful-api-designing-guidelines-the-best-practices-60e1d954e7c9
    return null;
  }

  @Override
  public void delete(User user) {
    CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
    usersCollectionReference.document(user.getId()).delete();
  }

}
