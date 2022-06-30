# Catan Board Generator Service
***
Back end prod URL: https://api.catanboardgenerator.com/  
Front end prod URL: https://catanboardgenerator.com
***
### Build Application
`mvn clean install`
***
### Run Application Locally
To run locally, run with Spring Active Profile `local`:
1. IntelliJ: **right click** on `CatanGeneratorServiceApplication.java` -> More Run/Debug -> Modify Run Configuration... -> set field "Active profiles" to `local`
2. Java Jar: `mvn clean install`, then `cd ../catan-generator-service/target` and run `java -jar -Dspring.profiles.active=local catan-generator-service-0.0.1-SNAPSHOT.jar`

Create file and add the following properties to `application-local.properties`:
````
contentful.accessToken=<access_token>
app.username=<anything>
app.password=<anything>
````
This file is automatically ignored by `.gitignore`
***
### Test Results & Coverage
To view test coverage report:
`../catan-generator-service/target/site/jacoco/index.html`. Open in Chrome.