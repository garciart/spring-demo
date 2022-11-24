# Spring Boot and DynamoDB Demo (Windows)

-----

## Steps:

-----

Requirement:

- Windows
- An Amazon Web Services (AWS) Developer account
- Java Development Kit (JDK), version 17

Create the project:

**NOTE** - If you have a development directory in your home directory, use that instead (e.g., ```mkdir --p ~/source/repos/spring-demo```, etc.).

```
mkdir ~/spring-demo
cd ~/spring-demo
git init
git branch -m main
touch README.md
wget https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore -o .gitignore
wget https://raw.githubusercontent.com/github/gitignore/main/Maven.gitignore -a .gitignore
git add --all :/
git commit -m "Initial commit."
git checkout -b devel
```

Configure AWS access:

```
msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi
aws configure
```

Enter the requested information when prompted:

```
AWS Access Key ID [None]: XXXXXXXXXXXXXXXXXXXX
AWS Secret Access Key [None]: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
Default region name [None]: us-east-1
Default output format [None]: json
```

This will create an AWS credentials profile file on your local system:

- Linux: ```~/.aws/credentials```
- Windows: ```C:\Users\USERNAME\.aws\credentials```

>**NOTE** - While you can also hard-code your credentials in the **application.properties** file (located at ```~/spring-demo/springdemo/src/main/resources/application.properties```), [it is not recommended](https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials_hardcoded.html).

Create a directory to hold the data scripts:

```
mkdir -p ~/spring-demo/data_scripts
cd ~/spring-demo/data_scripts
```

In that directory, create scripts and populate the DynamoDB database:

**NOTE** - This will create only one record. For multiple records, you can download **create-table-meds.json**, and both **batch-write-items-meds-25.json** and **batch-write-items-meds-50.json**, from the repository instead. Remember, [AWS only accepts 25 item put or delete operations per batch.](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_BatchWriteItem.html "BatchWriteItem")

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
cd ~/spring-demo
git add --all :/
git commit -m "Created database in AWS DynamoDB."
```

Visit https://start.spring.io/ to generate your Spring Boot project. The settings will be:

Project: Maven Project
Language: Java
Spring Boot: 2.7.5 (or the most recent stable version (i.e., not a RC or snapshot))
Project Metadata:
- Group: com.qmr
- Artifact: springdemo
- Name: springdemo
- Description: Spring Boot and DynamoDB Demo
- Package name: com.qmr.springdemo
- Packaging: Jar
- Java: 17
Dependencies: Add the following dependencies:
- Lombok
- Spring Web
- Thymeleaf

![Spring Initializr Page](images/01-spring-initializr.png "Spring Initializr Page")

Click on **GENERATE** to create the *springdemo.zip* file.

Unzip the file into a directory of your choosing:

**NOTE** - If you have a development directory in your home directory, use that instead (e.g., ```mkdir -p ~/Workspace/spring-demo```, etc.).

```
unzip ~/Downloads/springdemo.zip -d ~/spring-demo
```

Initialize the project:

```
cd ~/spring-demo
git init
git branch -m main
wget https://github.com/spring-projects/spring-boot/blob/main/.gitignore --output-document=.gitignore
git add --all :/
git commit -m "Initial commit."
git checkout -b devel
```

You will need additional dependencies. Using an editor of your choice, open **pom.xml** and, within the *<dependencies>* node, add the **AWS SDK For Java** (you can visit https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk to get the latest version):

```
<dependency>
	<groupId>com.amazonaws</groupId>
	<artifactId>aws-java-sdk</artifactId>
	<version>1.12.337</version>
</dependency>
```













Add a configuration directory and class to your project:

```
mkdir - p ~/spring-demo/springdemo/src/main/java/com/qmr/springdemo/configuration
touch ~/spring-demo/springdemo/src/main/java/com/qmr/springdemo/configuration/DynamoDBConfig.java
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