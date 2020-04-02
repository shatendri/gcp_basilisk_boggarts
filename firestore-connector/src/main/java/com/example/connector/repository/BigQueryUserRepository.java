package com.example.connector.repository;

import com.example.connector.config.BigQueryProperties;
import com.example.connector.domain.User;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BigQueryUserRepository {

    private final BigQuery bigQuery;
    private final BigQueryProperties bigQueryProperties;

    public BigQueryUserRepository(BigQuery bigQuery, BigQueryProperties bigQueryProperties) {
        this.bigQuery = bigQuery;
        this.bigQueryProperties = bigQueryProperties;
    }

    public List<User> findAll() throws InterruptedException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM " + bigQueryProperties.getDataSet() + "." + bigQueryProperties.getTable();
        QueryJobConfiguration queryConfig =
                QueryJobConfiguration.newBuilder(query).build();

        // Run the query using the BigQuery object
        for (FieldValueList row : bigQuery.query(queryConfig).iterateAll()) {
            User user = User.builder()
                    .id(row.get("id").getStringValue())
                    .firstName(row.get("first_name").getStringValue())
                    .lastName(row.get("last_name").getStringValue())
                    .gender(row.get("gender").getStringValue())
                    .email(row.get("email").getStringValue())
                    .ipAddress(row.get("ip_address").getStringValue())
                    .build();
            users.add(user);
        }
        return users;
    }
}
