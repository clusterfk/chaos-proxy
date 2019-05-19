# ClusterF*** - Chaos Proxy [![Build Status](https://travis-ci.org/AndyMacDroo/clusterf-chaos-proxy.svg?branch=master)](https://travis-ci.org/AndyMacDroo/clusterf-chaos-proxy)

ClusterF*** is a lightweight tool designed for chaos testing of microservices. 

* Configure your locally running _service-under-test_ to point to ClusterF*** and configure ClusterF*** to point to your real running _dependent-destination-service_. 

* Switch on ClusterF*** and configure a "chaos strategy".

* Watch the world burn :fire: :fire: :fire:

## Building Project ##

```sh
mvn clean install
```

## Configuration ##

Specify the port you would like to run **ClusterF***\** on:

```properties
server.port=8080 #8080 is the default
```

Use this information to configure your _service-under-test_ with relevant config for ClusterF*** in place of your _dependent-destination-service_.

### Example ###
For example, you might configure your _service-under-test_ to point to an image service which has been _"ClusterF***'d"_:

**Service Under Test Example Configuration**

If you configure your _service-under-test_ with properties files, they might change like so:

```properties
image.service=http://10.0.1.150:9898/image-service
```

Becomes:
```properties
image.service=http://localhost:8080/image-service
```

**Chaos Proxy Application Configuration**

Within ClusterF***, specify `application.properties` to point to your real destination service - e.g.:

```properties
destination.hostProtocolAndPort=http://10.0.1.150:9898
```
Specify your chaos strategy `NO_CHAOS, DELAY_RESPONSE, INTERNAL_SERVER_ERROR, BAD_REQUEST`:

```properties
chaos.strategy=DELAY_RESPONSE
```

If you are specifying a delayed response - you can specify the number of seconds with:

```properties
chaos.strategy.delay_response.seconds=60
```

## Running ##

* Build project:
```sh
mvn clean install
```

* Create a config directory containing your `application.properties` and take note of the directory name.

* Run application - swapping `<LOCATION_OF_CONFIG>` for the directory from the step before:
```sh
java -jar clusterf-chaos-proxy*.jar -Dspring.config.location=<LOCATION_OF_CONFIG>/config/application.properties
```