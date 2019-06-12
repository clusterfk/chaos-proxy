<img src="https://i.ibb.co/NsSNRv3/Webp-net-resizeimage-1.png" alt="91a16b7b-336f-402f-a78b-d194550da559" border="0"></a><br />
# ClusterFk Chaos Proxy

[![Build Status](https://travis-ci.org/clusterfk/chaos-proxy.svg?branch=master)](https://travis-ci.org/clusterfk/chaos-proxy) [![Docker Hub Pulls](https://img.shields.io/docker/pulls/andymacdonald/clusterf-chaos-proxy.svg)](https://hub.docker.com/r/andymacdonald/clusterf-chaos-proxy)

**ClusterFk** Chaos Proxy is an unreliable HTTP proxy you can rely on; a lightweight tool designed for chaos testing of microservices. 

## Why Would I Need This?

I will let you in on a secret: **everything fails eventually**. Micro-services often communicate with other services via REST and HTTP. 
How does your micro-service cope when the services it depends on inevitably fail?

## What Do I Do? (TLDR)

* Configure your locally running _service-under-test_ to point to the chaos proxy and configure the chaos proxy to point to your real running _dependent-destination-service_. 

* Switch on **ClusterFk** chaos proxy and configure a "chaos strategy".

* Watch the world burn :fire: :fire: :fire:

* _(Optional) Do something about it._


## Docker Image ##

Pull the latest image:

```sh
docker pull andymacdonald/clusterf-chaos-proxy
```

Then follow the steps in [Running](https://github.com/clusterfk/chaos-proxy#running) and [Configuration](https://github.com/clusterfk/chaos-proxy#configuration) to configure the proxy.

## Building ##

* Build project and create a new docker image:
```sh
mvn clean package && mvn docker:build
```

## Running ##

For running **ClusterFk** Chaos Proxy it is recommended you run using [Docker-Compose](https://docs.docker.com/compose/). Define a `docker-compose.yml` file such as the below:
```yaml
version: "3.7"
services:
  user-service-chaos-proxy:
    image: andymacdonald/clusterf-chaos-proxy
    environment:
      JAVA_OPTS: "-Dchaos.strategy=RANDOM_HAVOC -Ddestination.hostProtocolAndPort=http://localhost:8098"
    ports:
      - "8080:8080"
```

Then simply run (where the `docker-compose.yml` file is located):

```sh
docker-compose up
```

This will allow you to run multiple instances of **ClusterFk** Chaos Proxy - e.g:
```yaml
version: "3.7"
services:
  user-service-chaos-proxy:
    image: andymacdonald/clusterf-chaos-proxy
    environment:
      JAVA_OPTS: "-Dchaos.strategy=RANDOM_HAVOC -Ddestination.hostProtocolAndPort=http://10.0.0.231:8098"
    ports:
      - "8080:8080"
  account-service-chaos-proxy:
    image: andymacdonald/clusterf-chaos-proxy
    environment:
      JAVA_OPTS: "-Dchaos.strategy=DELAY_RESPONSE -Ddestination.hostProtocolAndPort=http://10.0.1.150:8918"
    ports:
      - "8081:8080"
```

## Running (Without Docker/Docker-Compose) ##

* Create a `config` directory containing your `application.properties` and take note of the directory name.

* Run application - swapping `<LOCATION_OF_CONFIG>` for the directory from the step before:
```sh
java -jar clusterf-chaos-proxy.jar -Dspring.config.location=<LOCATION_OF_CONFIG>/config/application.properties
```

## Configuration ##

Specify the port you would like to run **clusterfk** chaos proxy on:

```properties
server.port=8080 #8080 is the default
```

Use this information to configure your _service-under-test_ with relevant config for **ClusterFk** chaos proxy in place of your _dependent-destination-service_.

### Example ###

**Configure Chaos Proxy**

Within **ClusterFk** chaos proxy, specify `application.properties` or `JAVA_OPTS` to point to your real destination service - e.g.:

```properties
destination.hostProtocolAndPort=http://10.0.1.150:9898
```

**Chaos Strategies**

Specify your chaos strategy:

<pre><code><b>NO_CHAOS</b> - Request is simply passed through

<b>DELAY_RESPONSE</b> - Requests are delayed but successful (configurable delay)

<b>INTERNAL_SERVER_ERROR</b> - Requests return with 500 INTERNAL SERVER ERROR

<b>BAD_REQUEST</b> - Requests return with 400 BAD REQUEST

<b>RANDOM_HAVOC</b> - Requests generally succeed, but randomly fail with random HTTP status codes and random delays
</code></pre>

Within your `application.properties` or `JAVA_OPTS`:

```properties
chaos.strategy=DELAY_RESPONSE
```
**Delay Properties**

If you are specifying a delayed response - you can customise the behaviour of the delay with the following properties:

```properties
chaos.strategy.delayResponse.fixedPeriod=true       # if number of seconds to delay is constant or random (default is false)
chaos.tracing.headers=false                         # If this is set to true then 2 headers will be added to the request, 
                                                     'x-clusterfk-delayed-by' and 'x-clusterfk-status-code'.
chaos.strategy.delayResponse.seconds=60             # if fixed-period delay - number of seconds to delay each requests
chaos.strategy.delayResponse.random.maxSeconds=120  # if delay is random - maximum amount of time a request can last
```

**Configure Service Under Test**

For example, you might configure your _service-under-test_ to point to a user service which has been _"clusterfk'd"_.
If you configure your _service-under-test_ with properties files, they might change like so:

```properties
user-service.baseUrl=http://10.0.1.150:9898/user-service
```
To:
```properties
user-service.baseUrl=http://localhost:8080/user-service
```
