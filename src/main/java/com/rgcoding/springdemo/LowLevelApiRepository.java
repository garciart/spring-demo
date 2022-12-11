/**
 * Interface to perform CRUD operations with DynamoDB database
 */
package com.rgcoding.springdemo;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.*;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LowLevelApiRepository {
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private DynamoDB dynamoDB = new DynamoDB(client);
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private String tableName = "medications";

    /**
     * Initializes the DynamoDBlink class
     * 
     * @param tableName The DynamoDB table to use.
     */
    public LowLevelApiRepository() {
    }

    /**
     * Initializes the DynamoDBlink class
     * 
     * @param tableName The DynamoDB table to use.
     */
    public LowLevelApiRepository(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Method to create an item in the DynamoDB database table.
     * 
     * @param genericName  The generic name of the medication; also the partition key
     * @param brandName    The brand name of the medication
     * @param action       The medication's action
     * @param conditions   The conditions associated with the medication
     * @param bloodThinner If the medication is a blood thinner
     * @param schedule     The DEA restrictions of the medication
     * @param warnings     The warnings associated with the medication
     * @param interactions The interactions associated with the medication
     * @param sideEffects  The side effects associated with the medication
     * @param link         The link to the medlineplus.gov for more information about the medication
     * @return The json representation of the medication
     */
    public Item createItem( // NOSONAR
            String genericName, String brandName, String action, Set<String> conditions, boolean bloodThinner,
            int schedule, String warnings, String interactions, String sideEffects, String link) {
        Item item = null;
        try {
            Table table = dynamoDB.getTable(tableName);
            // Convert items in the conditions HashSet to uppercase
            // You must copy the HashSet to a separate object, to prevent Concurrent
            // Modification Exceptions
            ArrayList<String> temp = new ArrayList<>(conditions);
            for (String t : temp) {
                conditions.remove(t);
                conditions.add(t.toUpperCase());
            }

            // Table columns: generic_name, brand_name, action, conditions, blood_thinner,
            // schedule, warnings, interactions, side_effects, link)
            item = new Item().withPrimaryKey("generic_name", genericName.toUpperCase())
                    .withString("brand_name", brandName.toUpperCase()).withString("action", action.toUpperCase())
                    .withStringSet("conditions", conditions).withBoolean("blood_thinner", bloodThinner)
                    .withNumber("schedule", schedule).withString("warnings", warnings)
                    .withString("interactions", interactions).withString("side_effects", sideEffects)
                    .withString("link", link);
            table.putItem(item);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    (String.format("Error: Could not create '%s': %s", genericName.toUpperCase(), e.getMessage())));
        }
        return item;
    }

    /**
     * Method to retrieve all items from the DynamoDB database table.
     * 
     * @return The list of all the medications in the database in json format
     */
    public ScanResult retrieveAllItems() {
        ScanResult result = null;
        try {
            ScanRequest scanRequest = new ScanRequest().withTableName(tableName);
            result = client.scan(scanRequest);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    (String.format("Error: Could not retrieve items from '%s': %s", tableName, e.getMessage())));
        }
        return result;
    }

    /**
     * Method to retrieve an individual item from the DynamoDB database table.
     * 
     * @param genericName The generic name of the medication; also the partition key
     * @return The json representation of the medication
     */
    public Item retrieveItemByGenericName(String genericName) {
        Item item = null;
        try {
            Table table = dynamoDB.getTable(tableName);
            item = table.getItem("generic_name", genericName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    (String.format("Error: Could not read '%s': %s", genericName.toUpperCase(), e.getMessage())));
        }
        return item;
    }

    /**
     * Method to update an item in the DynamoDB database table.
     * 
     * @param genericName  The generic name of the medication; also the partition key
     * @param brandName    The brand name of the medication
     * @param action       The medication's action
     * @param conditions   The conditions associated with the medication
     * @param bloodThinner If the medication is a blood thinner
     * @param schedule     The DEA restrictions of the medication
     * @param warnings     The warnings associated with the medication
     * @param interactions The interactions associated with the medication
     * @param sideEffects  The side effects associated with the medication
     * @param link         The link to the medlineplus.gov for more information about the medication
     * @return The json representation of the medication
     */
    public Item updateItemByGenericName( // NOSONAR
            String genericName, String brandName, String action, Set<String> conditions, boolean bloodThinner,
            int schedule, String warnings, String interactions, String sideEffects, String link) {
        Item item = new Item();
        try {
            Table table = dynamoDB.getTable(tableName);
            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("generic_name", genericName.toUpperCase())
                    .withNameMap(new NameMap().with("#brand_name", "brand_name").with("#action", "action")
                            .with("#conditions", "conditions").with("#blood_thinner", "blood_thinner")
                            .with("#schedule", "schedule").with("#warnings", "warnings")
                            .with("#interactions", "interactions").with("#side_effects", "side_effects")
                            .with("#link", "link"))
                    .withValueMap(new ValueMap().with(":brandName", brandName.toUpperCase())
                            .with(":action", action.toUpperCase()).with(":conditions", conditions)
                            .with(":bloodThinner", bloodThinner).with(":schedule", schedule).with(":warnings", warnings)
                            .with(":interactions", interactions).with(":sideEffects", sideEffects).with(":link", link))
                    .withUpdateExpression("set #brand_name = :brandName," + "#action = :action,"
                            + "#conditions = :conditions," + "#blood_thinner = :bloodThinner,"
                            + "#schedule = :schedule," + "#warnings = :warnings," + "#interactions = :interactions,"
                            + "#side_effects = :sideEffects," + "#link = :link")
                    .withReturnValues(ReturnValue.ALL_NEW);
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            item = outcome.getItem();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    (String.format("Error: Could not update '%s': %s", genericName.toUpperCase(), e.getMessage())));
        }
        return item;
    }

    /**
     * Method to delete an item from the DynamoDB database table.
     * 
     * @param genericName The generic name of the medication; also the partition key
     * @return The json representation of the medication
     */
    public Item deleteItemByGenericName(String genericName) {
        Item item = new Item();
        try {
            Table table = dynamoDB.getTable(tableName);
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("generic_name", genericName.toUpperCase()).withReturnValues(ReturnValue.ALL_OLD);
            DeleteItemOutcome outcome = table.deleteItem(deleteItemSpec);
            item = outcome.getItem();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                    (String.format("Error: Could not update '%s': %s", genericName.toUpperCase(), e.getMessage())));
        }
        return item;
    }
}