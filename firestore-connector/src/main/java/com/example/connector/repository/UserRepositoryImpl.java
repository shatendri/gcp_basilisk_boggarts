package com.example.connector.repository;

import com.example.connector.domain.User;
import com.example.connector.util.UserMapToDtoConverter;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public List<User> findAll(Map<String, String> queryParams) throws ExecutionException, InterruptedException {

        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        Query query = usersCollectionReference;

        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }


        return Optional.of(query)
                .map(Query::get)
                .get()
                .get()
                .getDocuments().stream()
                .map(QueryDocumentSnapshot::getData)
                .map(map ->
                        map.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, val -> (String) val.getValue())))
                .map(UserMapToDtoConverter::convertFrom)
                .collect(Collectors.toList());
    }

    @Override
    public void save(User user) {
        Map<String, String> userMap = UserMapToDtoConverter.convertFrom(user);
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.add(userMap);
    }

    @Override
    public void update(User user) throws ExecutionException, InterruptedException {
        Map<String, String> userMap = UserMapToDtoConverter.convertFrom(user);
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.whereEqualTo("id", user.getId())
                .get().get().getDocuments().stream()
                .map(DocumentSnapshot::getReference)
                .findFirst()
                .ifPresent(dr -> dr.set(userMap));
    }

    @Override
    public void delete(String id) throws ExecutionException, InterruptedException {
        CollectionReference usersCollectionReference = firestore.collection(usersCollectionName);
        usersCollectionReference.whereEqualTo("id", id).get().get().getDocuments().stream()
                .map(DocumentSnapshot::getReference)
                .findFirst()
                .ifPresent(DocumentReference::delete);
    }

}
