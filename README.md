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
    - **Group:** com
    - **Artifact:** springdemo
    - **Name:** springdemo
    - **Description:** Demo project for Spring Boot and DynamoDB
    - **Package name:** com.springdemo
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
Expand-Archive ~/Downloads/springdemo.zip -d ~/source/repos
```

Initialize the project:

```
cd ~/source/repos/springdemo
git init
git branch -m main
# Spring Initializr created a .gitignore file; append the Spring.io .gitignore to it
Invoke-WebRequest -Uri https://raw.githubusercontent.com/spring-projects/spring-boot/main/.gitignore | Add-Content .gitignore
git add --all :/
git commit -m "Initial commit."
git checkout -b devel
```

Explore the application's directory structure. While you may see additional files and directories, here is a tree of the project files we will work with in this tutorial:

```
springdemo
+- src
   +- main
      +- java
         +- com
            +- springdemo
               +- SpringdemoApplication.java
      +- resources
         +- application.properties
         +- templates
         +- static
   +- test
      +- java
         +- com
            +- springdemo
               +- SpringdemoApplicationTest.java
+- target
+- mvnw
+- mvnw.cmd
+- pom.xml
```

You will need additional dependencies. Using an editor of your choice, open **pom.xml**, and, within the *<dependencies>* node, add the **AWS SDK For Java** (you can visit [https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk](https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk "Maven Repository") to get the latest version):

```
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-java-sdk</artifactId>
    <version>1.12.349</version>
</dependency>
```

To use a master layout for all pages, add the following dependency:

```
<dependency>
    <groupId>nz.net.ultraq.thymeleaf</groupId>
    <artifactId>thymeleaf-layout-dialect</artifactId>
</dependency>
```

Go to the **templates** directory:

```
cd ~/source/repos/springdemo/src/main/resources/templates
```


Using an editor of your choice, create a file named **index.html** and add the following code:

>**NOTE** - Spring Security requires a landing page. Otherwise, after you log in, you will get the message, "There was an unexpected error (type=Not Found, status=404)."

```
<!DOCTYPE html>
<html lang="en">
<head>
  <title>QMR</title>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width,initial-scale=1" />
  <meta name="description" content="" />
</head>
<body>
  <h1>Welcome to the Quick Medication Reference Spring Boot Demo</h1>
</body>
</html>
```

Go to your application's source directory and use Maven to create a Java ARchive file, known as a **jar** file, which Java can execute:

```
cd ~/source/repos/springdemo
./mvnw install
```

This will create the jar file in the **target** directory. Test out your application by entering the following command:

```
java -jar target/springdemo-0.0.1-SNAPSHOT.jar
```

>**NOTE** - Other developers may recommend that you use the command "./mvnw spring-boot:run" to run your application. Unfortunately, due to Windows path length limitations, that command may cause an error:
>
>```
>CreateProcess error=206, The filename or extension is too long
>```
>
>Creating and executing a jar is a safer and more dependable option.

>**NOTE** - If the message, "Error: JAVA_HOME not found in your environment" appears:
>
>1. Ensure you have installed [Java Development Kit 17](https://www.oracle.com/java/technologies/downloads/ "Java Development Kit 17").
>2. Get the JDK home directory:
>
>```
>java -XshowSettings:properties -version 2>&1 | findstr "java.home"
>```
>
>3. Once you have the directory and version number, enter the following command:
>
>```
>setx JAVA_HOME "<java.home value from the previous command>"
>setx PATH "%PATH%;%JAVA_HOME%\bin"
>```
>
>For example:
>
>```
>e.g., setx JAVA_HOME "C:\'Program Files'\Java\jdk-17.0.5"
>e.g., setx PATH "%PATH%;%JAVA_HOME%\bin";
>```
>
>For PowerShell
>
>```
>[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Progra~1\Java\jdk-17.0.5")
>[System.Environment]::SetEnvironmentVariable("Path", [System.Environment]::GetEnvironmentVariable('Path', [System.EnvironmentVariableTarget]::Machine) + ";$($env:JAVA_HOME)\bin")
>```

As the program runs, look for the following message:

```
Using generated security password: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

This generated password is for development use only. Your security configuration must be updated before running your application in production.
```

This is the password for your application's web site. Since you included Spring Security, your site is secured with a user name and password.

Open a browser and navigate to **localhost:8080**. When prompted, enter "user" for the user name and the generated password for the password.

