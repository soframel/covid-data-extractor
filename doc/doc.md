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
To run in development mode: 
`mvn quarkus:dev -Dquarkus.args=init`

To build the application: 

`mvn clean package`

To also build docker image:

 mvn clean package -Dquarkus.container-image.build=true 

NOTE: you should set the password to COVID user in scripts/.env file before building the image.

then run with: 

`docker run --name covid-extractor --network <NETWORK_NAME> <username>/covid-data-extractor:1.0-SNAPSHOT`
 
(no volume necessary)


NOTE: the 1rst time, execute the program with "init" at the end of the command to load all data since beginning of pandemy (this may take a while): 
`docker exec -it covid-extractor /bin/bash
/deployments/run-java.sh init
`
 