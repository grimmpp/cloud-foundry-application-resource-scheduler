# WORK IN PROGRESS 

# Cloud Foundry Resource Scheduler Service
The purpose of Resource Scheduler is to trigger endpoints after a defined timespan. There are some specific use cases implemented as service plans like restarting of applications or switching them off after a given time.
There is also a generic service plan available which allows to trigger arbitrary http endpoint. In the service <a href="./src/main/java/de/grimmpp/AppManager/config/CatalogConfig.java">catalog</a> you can find the list of supported service plans. 

The Resource Scheduler consists of two parts:
1. Service Broker which can be registered in the Cloud Foundry marketplace and all service can be regularly booked via Cloud Foundry commands.
2. Scheduler which triggers the defined endpoints and time periodes. What kind of endpoint will be triggered is defined in each service plan.

Both parts, Service Broker and Scheduler, is contained within the same application. You only need to deploy one applicatin into Cloud Foundry.

## Technical data
* Used Spring Cloud Open **Service Broker API**
* **Spring Boot** is used as Java Framework
  * **Spring Boot Security** (Basic Auth) is implemented.
  * **Spring Boot JPA & Hibernate** is used for DB connection.
  * **Cloud Foundry API Client** is self-developed and contained within this project. (This was done because I wanted to be able to run all junit tests locally.)
* In the section/module test there is an additional RestController which **mocks** the **Cloud Foundry API** in order to test the full roundtrip of API calls to Cloud Foundry. (OAuth tests are not included.)
* **Lombock** is used to keed class definitions simpler.


## How to build and run unit tests
````
./gradlew clean build
````

## Prerequisites for depolying 
For deploying this application you require **Cloud Foundry** and a **relational database**.

## What to configure
Check out application.yml there you can find the necessary environment variable to be set:
* BROKER_BASIC_AUTH_USERNAME
* BROKER_BASIC_AUTH_PASSWORD
* CF_ADMIN_USERNAME
* CF_ADMIN_PASSWORD

You may need to set hibernate dialects like: 
* spring.jpa.properties.hibernate.dialect: org.hibernate.dialect.MariaDBDialect

## How to deploy
For cfdev see: <a href="./deployIntoCfdev/deploy.bat">./deployIntoCfdev/deploy.bat</a>

## Links
* Service Broker Api Framework: https://spring.io/projects/spring-cloud-open-service-broker
* cfdev for installing Cloud Foundry locally: https://github.com/cloudfoundry-incubator/cfdev
* cfdev Cloud Foundry CLI Plugin: https://plugins.cloudfoundry.org/#cfdev 
