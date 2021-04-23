# PLYE - Pretty Lazy Yelp Extractor

## Dependencies

* Java 11
* Maven 3.6.3

## Installation

Navigate to root directory (where the topmost pom.xml is located) and run:

> mvn clean package
> 
> java -jar ./target/plye-0.0.1-SNAPSHOT.jar


## Environment Setup

In order to run locally, you will need to configure two environment variables:

* [GOOGLE_APPLICATION_CREDENTIALS](https://cloud.google.com/docs/authentication/getting-started)
    * Path to JSON file containing Google Cloud credentials.
* [YELP_FUSION_API_KEY](https://www.yelp.com/developers/documentation/v3/authentication)
    * String containing you Yelp Fusion API Key
  
## Demo

Point your browser to https://nard.casa to see a working demo, takes a second or two to process requests.
