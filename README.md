# FitTracker
App for tracking your weight and calorie intake.

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
    ./mvnw clean install
    ``` 
2. Run **FitTrackerApplication.java** in your IDE (with active profile `local`) or use the command:
    ```
    ./mvnw spring-boot:run -Dspring.profiles.active=local
    ``` 
3. Navigate to http://localhost:8080/health, you should see:
    ```
    {
        "status": "UP"
    }
    ``` 