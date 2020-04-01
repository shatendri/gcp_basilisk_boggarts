package com.example.connector.repository;

import com.example.connector.config.BigQueryProperties;
import com.example.connector.domain.User;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValue;
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
            User user = User.builder().build();
            for (FieldValue val : row) {
                user.setId(row.get("id").getStringValue());
                user.setFirstName(row.get("first_name").getStringValue());
                user.setLastName(row.get("last_name").getStringValue());
                user.setGender(row.get("gender").getStringValue());
                user.setEmail(row.get("email").getStringValue());
                user.setIpAddress(row.get("ip_address").getStringValue());
                System.out.println(val);
            }
            users.add(user);
        }
        return users;
    }
}
