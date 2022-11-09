/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qmr.springdemo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author Rob
 */
public class DynamoDBLink {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);

    static String tableName = "medications";

    public static ItemCollection<ScanOutcome> getItems() {
        Table table = dynamoDB.getTable(tableName);
        ItemCollection<ScanOutcome> allItems = table.scan();
        return allItems;
    }

    public static PutItemOutcome createItem(String genericName,
            String brandName,
            String action,
            ArrayList<String> conditions,
            String sideEffects,
            String interactions,
            String warnings,
            String link,
            int schedule,
            Boolean bloodThinner) {
        Table table = dynamoDB.getTable(tableName);
        Item item = new Item().withPrimaryKey("generic_name", genericName)
                .withString("brand_name", brandName)
                .withString("action", action)
                .withStringSet("conditions", new HashSet<>(conditions))
                .withString("side_effects", sideEffects)
                .withString("action", interactions)
                .withString("interactions", warnings)
                .withString("link", link)
                .withNumber("schedule", schedule)
                .withBoolean("blood_thinner", bloodThinner);
        return table.putItem(item);
    }

    public static String readItem(String genericName) {
        Table table = dynamoDB.getTable(tableName);
        Item item = table.getItem("generic_name", genericName);
        return item.toJSONPretty();
    }

    public static String updateItem(String genericName,
            String brandName,
            String action,
            ArrayList<String> conditions,
            String sideEffects,
            String interactions,
            String warnings,
            String link,
            int schedule,
            Boolean bloodThinner) {
        Table table = dynamoDB.getTable(tableName);

        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#B", "brand_name");
        expressionAttributeNames.put("#A", "action");
        expressionAttributeNames.put("#C", "conditons");
        expressionAttributeNames.put("#S", "side_effects");
        expressionAttributeNames.put("#I", "interactions");
        expressionAttributeNames.put("#W", "warnings");
        expressionAttributeNames.put("#L", "link");
        expressionAttributeNames.put("#S", "schedule");
        expressionAttributeNames.put("#T", "blood_thinner");

        Map<String, Object> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":val1", brandName);
        expressionAttributeValues.put(":val2", action);
        expressionAttributeValues.put(":val3",
                new HashSet<>(new HashSet<>(conditions)));
        expressionAttributeValues.put(":val4", sideEffects);
        expressionAttributeValues.put(":val5", interactions);
        expressionAttributeValues.put(":val6", warnings);
        expressionAttributeValues.put(":val7", link);
        expressionAttributeValues.put(":val8", schedule);
        expressionAttributeValues.put(":val9", bloodThinner);

        UpdateItemOutcome outcome;
        outcome = table.updateItem(
                "generic_name", genericName,
                "set #B = :val1 set #A = :val2 set #C = :val3 set #S = :val4 set #I = :val5 set #W = :val6 set #L = :val7 set #S = :val8 set #T = :val9",
                expressionAttributeNames,
                expressionAttributeValues);
        return outcome.getItem().toJSONPretty();
    }

    public static String deleteItem(String genericName) {
        Table table = dynamoDB.getTable(tableName);
        DeleteItemOutcome outcome = table.deleteItem("generic_name", genericName);
        return outcome.getItem().toJSONPretty();
    }
    
    public static void main(String[] args) throws IOException {
         System.out.println(readItem("ACYCLOVIR"));
   }
}
