# Spring Boot and DynamoDB Demo (Windows)

>**NOTE** - For a similar project, using AWS, Linux, Python, and Flask, see https://github.com/garciart/flask-demo.

-----

## Steps:

-----

Requirement:

- Windows 10+ with PowerShell (Tested on Windows 11 with PS 5.1)
- An Amazon Web Services (AWS) Developer account
- Java Development Kit (JDK), version 17

Create the project:

Visit https://start.spring.io/ to generate your Spring Boot project. The settings will be:

Project: Maven Project
Language: Java
Spring Boot: The most recent stable version (i.e., not a release candidate (RC) or snapshot))
Project Metadata:
- Group: com.rgcoding
- Artifact: springdemo
- Name: springdemo
- Description: Demo project for Spring Boot and DynamoDB
- Package name: com.rgcoding.springdemo
- Packaging: Jar
- Java: 17
Dependencies: Add the following dependencies:
- Lombok
- Spring Web
- Thymeleaf

![Spring Initializr Page](images/01-spring-initializr.png "Spring Initializr Page")

Click on **GENERATE** to create the *springdemo.zip* file.

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

You will need additional dependencies. Using an editor of your choice, open **pom.xml** and, within the *<dependencies>* node, add the **AWS SDK For Java** (you can visit https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk to get the latest version):

```
<dependency>
	<groupId>com.amazonaws</groupId>
	<artifactId>aws-java-sdk</artifactId>
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
chmod 666 *.json
aws dynamodb create-table --cli-input-json file://create-table-meds.json
aws dynamodb batch-write-item --request-items file://batch-write-items-meds.json
```

**OUTPUT:**

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















Add a configuration directory and class to your project:

```
mkdir - p ~/springdemo/springdemo/src/main/java/com/qmr/springdemo/configuration
touch ~/springdemo/springdemo/src/main/java/com/qmr/springdemo/configuration/DynamoDBConfig.java
```

Using an editor of your choice, open **DynamoDBConfig.java** and add the following code:

```

```





Also, add Sebastian J's excellent **Spring Data DynamoDB** module (you can visit https://github.com/derjust/spring-data-dynamodb to get the latest version):

```
<dependency>
    <groupId>com.github.derjust</groupId>
    <artifactId>spring-data-dynamodb</artifactId>
    <version>5.1.0</version>
</dependency>
```

REFERENCES:
https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/JavaDocumentAPICRUDExample.html
https://www.baeldung.com/spring-data-dynamodb
https://spring.io/guides/gs/serving-web-content/
https://www.baeldung.com/spring-boot-start
https://datatables.net/examples/styling/bootstrap5.html