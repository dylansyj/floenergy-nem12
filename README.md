# FloEnergy Demo

## Project Description
This repository contains a demo project for FloEnergy

## Prerequisites

- JDK 24 [https://www.oracle.com/sg/java/technologies/downloads/]
- Maven

## Installation
To install and run this project, follow the steps below:
1. Clone the repository.
   ```bash
   git clone https://github.com/dylansyj/floenergy-nem12.git
   cd Nem12Parser
   ```
2. Run `mvn clean package` at same folder level as pom.xml
3. Run `java -jar target/Nem12Parser-1.0-SNAPSHOT.jar`

## Usage
1. Use postman, send request to localhost:8080/parser/zip with form-data
    1. key: file, value : file_in_zip

## Q&A
1. What is the rationale for the technologies you have decided to use? 
    - Using Java allows for stricter type safety as compared to python (which has lesser boilerplate)

2. What would you have done differently if you had more time?
    - Adding of unit tests with comprehensive test data

3. What is the rationale for the design choices that you have made?
    - Quite straightforward design, if it needs to be event driven we can substitute out the controller with an eventlistener.
    - Using UUID from Java instead of MySQL, as it is not system critical and the chances of clash is near zero. Implemention of the UUID is straightforward in this scenario.
    - Reading from ZIP File as it can compress huge data set, grouping insert statements and flushing in batches. This ensures that the local memory won't go OOM (we wait to populate all the records first and then flushing it, it relies heavily on local memory).