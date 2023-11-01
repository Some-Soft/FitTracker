# FitTracker

App for tracking your weight and calorie intake.

## Code

Google Style is used. Config can be found in **intellij-java-google-style.xml** located in the root directory.

## Local Setup

### Prerequisites

* Java 21
* Maven
* Docker

### Database

1. To build and run containers (PostgreSQL & pgAdmin4):

```
docker-compose up -d
```

2. Go to pgAdmin on http://localhost:5050, login with:
    * email: **user@example.com**
    * password: **password**
3. Right-click **Servers** -> **Register** -> **Server**
4. In **General** type in any **_name_**. In **Connection**:
    * Set Host name/adress to **postgres**
    * Set Port to **5432**
    * Set Username to **user**
    * Set Password to **password**
5. Click Save
6. To test connection, choose **fittracker** from left tab. From top bar choose: **Tools** -> **Query Tool**
7. Query tab should appear. Write a query:
    ```
    SELECT 42
    ```
9. After running query with **"play"** sign, number 42 should appear in first row in first column

### SpringBoot App

1. Build the project:
    ```
    ./mvnw clean install -DskipTests
    ``` 
2. Run **FitTrackerApplication.java** in your IDE or use the command:
    ```
    ./mvnw spring-boot:run
    ``` 
3. Navigate to http://localhost:8080/health, you should see:
    ```
    {
        "status": "UP"
    }
    ```

### Docker

FitTracker is containerized, use the command to build a docker image:

`docker build -t fittracker .`

In order to run the app in a container you can use docker compose:

```
docker-compose up -d
```

or run the container with the command:

`docker run --network=fittracker -e DB_HOST=postgres -p 8080:8080 fittracker`

#### Updating Dockerfile

As we progress with development we'll be adding new modules to the application.
Therefore, these modules will have to be added to Dockerfile (which includes only what's necessary).
To do that, we'll have to:

1. Build the jar

```
mvn clean package -DskipTests 
```

2. Unpack the jar
```
jar xf target/fittracker-0.0.1-SNAPSHOT.jar
```

3. Use jdeps to get dependencies:

```
jdeps --ignore-missing-deps -q  \
--recursive  \
--multi-release 21  \
--print-module-deps  \
--class-path 'BOOT-INF/lib/*'  \
target/fittracker-0.0.1-SNAPSHOT.jar
```

4. Update modules in Dockerfile (`--add-modules` option)
