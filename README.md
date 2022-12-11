# Spring Boot and DynamoDB Demo (Windows)

>**NOTE** - For a similar project, using AWS, Linux, Python, and Flask, see https://github.com/garciart/flask-demo.

-----

## Steps:

Requirement:

- Windows 10+ with PowerShell (Tested on Windows 11 with PS 5.1)
- An Amazon Web Services (AWS) Developer account
- Java Development Kit (JDK), version 17

Create the project:

Visit [https://start.spring.io/](https://start.spring.io/ "Spring Initializr") to generate your Spring Boot project. The settings will be:

- **Project:** Maven Project
- **Language:** Java
- **Spring Boot:** The most recent stable version (i.e., not a release candidate (RC) or snapshot))
- **Project Metadata:**
    - **Group:** com.rgcoding
    - **Artifact:** springdemo
    - **Name:** springdemo
    - **Description:** Demo project for Spring Boot and DynamoDB
    - **Package name:** com.rgcoding.springdemo
    - **Packaging:** Jar
    - **Java:** 17
- **Dependencies:** Add the following dependencies:
    - Lombok
    - Spring Web
    - Thymeleaf

![Spring Initializr Page](images/01-spring-initializr.png "Spring Initializr Page")

Click on **GENERATE** to create and download the **springdemo.zip** file.

Unzip the file into a your development directory:

**NOTE** - If you have a development subdirectory in your home directory, use that instead (e.g., ```mkdir --p ~/source/repos/springdemo```, etc.).

```
Expand-Archive ~/Downloads/springdemo.zip -d ~/
```

Initialize the project:

```
cd ~/springdemo
git init
git branch -m main
# Spring Initializr created a .gitignore file; append the Spring.io .gitignore to it
Invoke-WebRequest -Uri https://raw.githubusercontent.com/spring-projects/spring-boot/main/.gitignore | Add-Content .gitignore
git add --all :/
git commit -m "Initial commit."
git checkout -b devel
```

You will need additional dependencies. Using an editor of your choice, open **pom.xml** and, within the *<dependencies>* node, add the **AWS SDK For Java** (you can visit [https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk](https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk "Maven Repository") to get the latest version):

```
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.12.349</version>
</dependency>
```

To use a master layout for all pages:

```
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

Also, add Sebastian J's excellent **Spring Data DynamoDB** module (you can visit https://github.com/derjust/spring-data-dynamodb to get the latest version):

```
<dependency>
    <groupId>com.github.derjust</groupId>
    <artifactId>spring-data-dynamodb</artifactId>
    <version>5.1.0</version>
</dependency>
```

Create a directory to hold the data scripts:

```
mkdir -p ~/springdemo/data_scripts
cd ~/springdemo/data_scripts
```

In that directory, create scripts and populate the DynamoDB database:

>**NOTE** - This will create only one record. For multiple records, you can download **create-table-meds.json**, and both **batch-write-items-meds-25.json** and **batch-write-items-meds-50.json**, from the repository instead. Remember, [AWS only accepts 25 item put or delete operations per batch.](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_BatchWriteItem.html "BatchWriteItem")
>
>- ```Invoke-WebRequest -Uri https://raw.githubusercontent.com/garciart/springdemo/main/data_scripts/create-table-meds.json -OutFile ~/eclipse-workspace/springdemo/data_scripts/create-table-meds.json```
>- ```Invoke-WebRequest -Uri https://raw.githubusercontent.com/garciart/springdemo/main/data_scripts/batch-write-items-meds-25.json -OutFile ~/eclipse-workspace/springdemo/data_scripts/batch-write-items-meds-25.json```
>- ```Invoke-WebRequest -Uri https://raw.githubusercontent.com/garciart/springdemo/main/data_scripts/batch-write-items-meds-50.json -OutFile ~/eclipse-workspace/springdemo/data_scripts/batch-write-items-meds-50.json```

```
echo '{ "TableName": "medications", "KeySchema": [ { "KeyType": "HASH", "AttributeName": "generic_name" } ], "AttributeDefinitions": [ { "AttributeName": "generic_name", "AttributeType": "S" } ], "BillingMode": "PAY_PER_REQUEST" }' > create-table-meds.json
echo '{ "medications": [ { "PutRequest": { "Item": { "generic_name": { "S": "ACYCLOVIR" }, "brand_name": { "S": "ZOVIRAX" }, "action": { "S": "ANTIVIRAL" }, "conditions": { "SS": [ "HERPES", "COLD SORES" ] }, "schedule": { "N": "0" }, "blood_thinner": { "S": "FALSE" }, "side_effects": { "S": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." }, "interactions": { "S": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." }, "warnings": { "S": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." }, "link": { "S": "https://medlineplus.gov/druginfo/meds/a681045.html" } } } } ] }' > batch-write-items-meds.json
aws dynamodb create-table --cli-input-json file://create-table-meds.json
aws dynamodb batch-write-item --request-items file://batch-write-items-meds.json
```

**Output:**

```
> aws dynamodb create-table --cli-input-json file://create-table-meds.json
{
    "TableDescription": {
        "AttributeDefinitions": [
            {
                "AttributeName": "generic_name",
                "AttributeType": "S"
            }
        ],
        "TableName": "medications",
        "KeySchema": [
            {
                "AttributeName": "generic_name",
                "KeyType": "HASH"
            }
        ],
        "TableStatus": "CREATING",
        "CreationDateTime": 1667847213.294,
        "ProvisionedThroughput": {
            "NumberOfDecreasesToday": 0,
            "ReadCapacityUnits": 0,
            "WriteCapacityUnits": 0
        },
        "TableSizeBytes": 0,
        "ItemCount": 0,
        "TableArn": "arn:aws:dynamodb:us-east-1:XXXXXXXXXXXX:table/medications",
        "TableId": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
        "BillingModeSummary": {
            "BillingMode": "PAY_PER_REQUEST"
        }
    }
}
> aws dynamodb batch-write-item --request-items file://batch-write-items-meds-25.json
{
    "UnprocessedItems": {}
}
```

Continue to add items using **batch-write-item**. If DynamoDB returns any unprocessed items, check your JSON script and try again.

Save your work and update your requirements:

```
cd ~/springdemo
git add --all :/
git commit -m "Created database in AWS DynamoDB."
```

Navigate to main application directory:

```
cd ~\springdemo\src\main\java\com\rgcoding\springdemo
```

Create the following subdirectories:

```
mkdir configuration
mkdir domain
mkdir repository
mkdir controller
```

Access the configuration subdirectory:

```
cd ~\springdemo\src\main\java\com\rgcoding\springdemo\configuration
```

Using an editor of your choice, create a class named **config.java** and add the following code:

```
```



Access the domain subdirectory:

```
cd ~\springdemo\src\main\java\com\rgcoding\springdemo\domain
```

Using an editor of your choice, create a class named **Medication.java** and add the following code:

>**NOTE** - While you can create, read, update, and delete DynamoDB items directly with the AWS SDK for Java Document API, a model will allow you to centralize and avoid repetition of tasks, such as input validation and capitalization.

```
/**
 * Model for medication items.
 */
package com.rgcoding.springdemo.domain;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.rgcoding.springdemo.Validator;

@DynamoDBTable(tableName = "medications")
public class Medication {
    private String genericName;
    private String brandName;
    private String action;
    private Set<String> conditions;
    private Boolean bloodThinner;
    private Integer schedule;
    private String warnings;
    private String interactions;
    private String sideEffects;
    private String link;

    // Lookahead to prevent injections that use dashes or pluses (e.g., 'rm -rf', etc.)
    String lookaheadRegex = "(?!.+?(?: -|\\- | \\+|\\+ |\\+\\-|\\-\\+|\\-\\-).+?)";
    // Short descriptions must start and end with a letter
    // Details must start with a letter and end with a letter or period
    // RegEx for name, action, and condition
    String shortDescRegex = "^" + lookaheadRegex + "([A-Z])([A-Z\\d\\-\\+ ]{1,32})([A-Z])$";
    // RegEx for interactions, side effects, and warnings
    String detailsRegex = "^" + lookaheadRegex + "([A-Z])([A-Z\\d\\-\\+\\,\\. ]{1,255})([A-Z\\.])$";
    String linkRegex = "^http([s]?)\\:\\/\\/[A-Za-z\\d\\.\\_\\-\\/]{1,255}$";

    // Partition key
    @DynamoDBHashKey(attributeName = "generic_name")
    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        Validator.validateRegexMatch(genericName, shortDescRegex);
        this.genericName = genericName.toUpperCase();
    }

    @DynamoDBHashKey(attributeName = "brand_name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        Validator.validateRegexMatch(brandName, shortDescRegex);
        this.brandName = brandName.toUpperCase();
    }

    @DynamoDBHashKey(attributeName = "action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        Validator.validateRegexMatch(action, shortDescRegex);
        this.action = action.toUpperCase();
    }

    @DynamoDBHashKey(attributeName = "conditions")
    public Set<String> getConditions() {
        return conditions;
    }

    public void setConditions(Set<String> conditions) {
        for (String c : conditions) {
            Validator.validateRegexMatch(c, shortDescRegex);
        }
        this.conditions = this.convertSetToUpper(conditions);
    }

    @DynamoDBHashKey(attributeName = "blood_thinner")
    public Boolean getBloodThinner() {
        return bloodThinner;
    }

    public void setBloodThinner(Boolean bloodThinner) {
        this.bloodThinner = bloodThinner;
    }

    @DynamoDBHashKey(attributeName = "schedule")
    public Integer getSchedule() {
        return schedule;
    }

    public void setSchedule(Integer schedule) {
        Validator.validateInteger(schedule, 0, 4);
        this.schedule = schedule;
    }

    @DynamoDBHashKey(attributeName = "warnings")
    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        Validator.validateRegexMatch(warnings, detailsRegex);
        this.warnings = warnings;
    }

    @DynamoDBHashKey(attributeName = "interactions")
    public String getInteractions() {
        return interactions;
    }

    public void setInteractions(String interactions) {
        Validator.validateRegexMatch(interactions, detailsRegex);
        this.interactions = interactions;
    }

    @DynamoDBHashKey(attributeName = "side_effects")
    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        Validator.validateRegexMatch(sideEffects, detailsRegex);
        this.sideEffects = sideEffects;
    }

    @DynamoDBHashKey(attributeName = "link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        Validator.validateRegexMatch(link, linkRegex);
        this.link = link;
    }

    private Set<String> convertSetToUpper(Set<String> rawSet) {
        // Convert items in a set to uppercase
        // You must copy the set to a separate object,
        // to prevent Concurrent Modification Exceptions
        Set<String> temp = rawSet;
        for (String t : temp) {
            rawSet.remove(t);
            rawSet.add(t.toUpperCase());
        }
        return temp;
    }
}
```

Access the repository subdirectory:

```
cd repository
```

Using an editor of your choice, create a class named **MedicationRepository.java** and add the following code:

```
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

public class DynamoDBLink {
    private AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private DynamoDB dynamoDB = new DynamoDB(client);
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private String tableName = "";

    /**
     * Initializes the DynamoDBlink class
     * 
     * @param tableName The DynamoDB table to use.
     */
    public MedicationRepository(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Method to create an item in the DynamoDB database table.
     * 
     * @param genericName The generic name of the medication; also the partition key
     * @param brandName The brand name of the medication
     * @param action The medication's action
     * @param conditions The conditions associated with the medication
     * @param bloodThinner If the medication is a blood thinner
     * @param schedule The DEA restrictions of the medication
     * @param warnings The warnings associated with the medication
     * @param interactions The interactions associated with the medication
     * @param sideEffects The side effects associated with the medication
     * @param link The link to the medlineplus.gov for more information about the medication
     * @return The json representation of the medication
     */
    public Item createItem(String genericName, String brandName, String action, Set<String> conditions,
            boolean bloodThinner, int schedule, String warnings, String interactions, String sideEffects, String link) {
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
     * Method to retrieve an individual item from the DynamoDB database table.
     * 
     * @param genericName The generic name of the medication; also the partition key
     * @return item The json representation of the medication
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
     * @param genericName The generic name of the medication; also the partition key
     * @param brandName The brand name of the medication
     * @param action The medication's action
     * @param conditions The conditions associated with the medication
     * @param bloodThinner If the medication is a blood thinner
     * @param schedule The DEA restrictions of the medication
     * @param warnings The warnings associated with the medication
     * @param interactions The interactions associated with the medication
     * @param sideEffects The side effects associated with the medication
     * @param link The link to the medlineplus.gov for more information about the medication
     * @return The json representation of the medication
     */
    public Item updateItemByGenericName(String genericName, String brandName, String action, Set<String> conditions,
            boolean bloodThinner, int schedule, String warnings, String interactions, String sideEffects, String link) {
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
```













Add a configuration directory and class to your project:

```
mkdir - p ~/springdemo/springdemo/src/main/java/com/qmr/springdemo/configuration
touch ~/springdemo/springdemo/src/main/java/com/qmr/springdemo/configuration/DynamoDBConfig.java
```








**REFERENCES:**

- https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/CodeSamples.Java.html
- https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaDocumentAPICRUDExample.html

https://www.baeldung.com/spring-data-dynamodb
https://spring.io/guides/gs/serving-web-content/
https://www.baeldung.com/spring-boot-start
https://datatables.net/examples/styling/bootstrap5.html