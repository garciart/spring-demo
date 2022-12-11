package com.rgcoding.springdemo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;

@Repository
public class SpringdemoRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;
    static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Method to retrieve all items from the DynamoDB database table.
     * 
     * <ul>
     * <li>You do not need to import or instantiate the Medication class
     * <li>The Spring Container does that for you through Inversion of Control (IoC) and Beans
     * <li>Spring (IoC) Container: Manages beans and instantiates objects
     * <li>Spring Bean: An instance of a class managed by the Spring Container; identified by @Annotations
     * </ul>
     * 
     * @return A list of all Medication objects in the database.
     */
    public List<Medication> readAllItems() {
        List<Medication> medications = null;
        try {
            // Get the data in from the database
            PaginatedScanList<Medication> scanResult = dynamoDBMapper.scan(Medication.class,
                    new DynamoDBScanExpression());
            medications = scanResult;
            for (Medication m : medications) {
                System.out.println(m.getBrandName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, (String.format("Error: Could not retrieve items: %s", e.getMessage())));
        }
        return medications;
    }
}
