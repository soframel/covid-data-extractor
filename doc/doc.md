# Covid Data Extractor technical documentation

Use on an existing Elasticsearch + kibana cluster

##Index creation & configuration in ElasticSearch
Index creation in Elasticsearch: 
PUT on <host>:9200/covid with body=elastic/mapping.json

then create the Index Pattern in Kibana: Create Index Pattern "covid*":
* choose covid index
* time field = date

then create role in Kibana: 
* create role covid-role
* add index privilege to index "covid" to "all"

then create user in Kibana: 
* create user/password
* add created role + role "transport_client"

## Build & Start extractor
Build application: 

`mvn clean package`

Then build docker image:

 mvn clean package -Dquarkus.container-image.build=true 

the run with: 

`docker run --name covid-extractor --e elastic.password=<YOURPASSWORD>`
 
(no volume necessary)


NOTE: the 1rst time, start with parameter "init" to load all data since beginning of pandemy (this may take a while).
TODO: show how. 

## Quarkus development
This project uses Quarkus, the Supersonic Subatomic Java Framework.


If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

##3 Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

To run with initialization mode: 
```
./mvnw quarkus:dev -Dquarkus.args=init
```

##3 Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `covid-data-extractor-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/covid-data-extractor-1.0-SNAPSHOT-runner.jar`.

##3 Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/covid-data-extractor-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.