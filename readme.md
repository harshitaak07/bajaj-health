This repository contains my submission for the HealthRx / Bajaj Finserv Hiring Assessment (Java).  

-> The JAR File is located inside the output folder.
-> The output of the response is given in the image below:
![WhatsApp Image 2025-12-01 at 16 51 11_72f4c608](https://github.com/user-attachments/assets/95f67212-d4a8-46e2-aaab-2607df1e5e61)

## Features 

### 1. Automatic Execution on Startup
- No controllers or endpoints trigger the logic.

### 2. Calls the Generate Webhook API
On startup, the app sends:
POST [https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA](https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA)
With registration details:
```json
{
  "name": "<YOUR_NAME>",
  "regNo": "<YOUR_REGNO>",
  "email": "<YOUR_EMAIL>"
}
````
The API returns:
* `webhook` (custom submission URL)
* `accessToken` (authentication token)

### 3. Processes SQL Question
Based on the assigned question (from the PDF), the app:
* Computes the SQL answer
* Stores it as **finalQuery.sql** locally
* Sends it back to the generated webhook

### 4. Submits Final Query
The app sends:
```
POST <webhook_from_API>
Authorization: <accessToken>
```
Body:
```json
{
  "finalQuery": "<SQL_QUERY_STRING>"
}
```

## File Structure
```
bfhl/
 ├── src/
 │    ├── main/
 │    │     ├── java/com/example/bfhl/
 │    │     │     ├── BfhlApplication.java
 │    │     │     └── Runner.java
 │    │     └── resources/application.properties
 ├── output/
 │     └── bfhl-0.0.1-SNAPSHOT.jar   <-- FINAL JAR FILE
 ├── finalQuery.sql                  <-- Generated at runtime
 ├── pom.xml
 └── README.md
```

