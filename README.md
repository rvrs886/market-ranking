## Requirements
* internet access (app is using kanga api to fetch required data)
* java 25
* maven 3.9.6+

## Run application

### Using maven
* run in terminal
  * `mvn spring-boot:run`

### Using jar
* run in terminal
  * `mvn clean package`
  * `java -jar <project-directory>/target/market-ranking-0.0.1-SNAPSHOT.jar`

## Test requests

1. Market ranking calculation request
   ```
   curl -X POST "http://localhost:8080/api/spread/calculate" -H "Authorization: Bearer ABC123"
   ``` 
2. Fetch market ranking request
   ```
   curl -X GET "http://localhost:8080/api/spread/ranking" -H "Authorization: Bearer ABC123"
   ```
3. Sample of unauthorized request
   ```
   curl -X GET "http://localhost:8080/api/spread/ranking" -H "Authorization: Bearer WRONG"
   ```