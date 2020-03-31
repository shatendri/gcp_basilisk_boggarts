package com.example.connector.repository;

import com.example.connector.domain.User;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BigQueryUserRepository {

    @Autowired
    BigQuery bigQuery;

    public Mono<List<User>> findAll() throws InterruptedException {

        String query = "SELECT column FROM table;";
        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(query).build();

        // Run the query using the BigQuery object
        for (FieldValueList row : bigQuery.query(queryConfig).iterateAll()) {
            for (FieldValue val : row) {
                System.out.println(val);
            }
        }
        return null;
    }
}
