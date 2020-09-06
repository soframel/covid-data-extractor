# covid-data-extractor project

This utility can inject covid-19 data from different sources into an ElasticSearch index. 

It currently injects: 
 * for France, data from https://coronavirusapi-france.now.sh
 * for Luxembourg, data from https://data.public.lu/fr/datasets CSV dataset 

It can be initialized with all data since 03/2020. In addition, when run everyday, it will fetch and inject the latest data for the given day.  


It requires a running ElasticSearch instance. 

For technical documentation, see [doc/doc.md]


