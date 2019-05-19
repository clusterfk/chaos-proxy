<img src="https://i.ibb.co/WtvPm26/91a16b7b-336f-402f-a78b-d194550da559.png" alt="91a16b7b-336f-402f-a78b-d194550da559" border="0"></a><br />
# ClusterF*** Chaos Proxy
[![Build Status](https://travis-ci.org/AndyMacDroo/clusterf-chaos-proxy.svg?branch=master)](https://travis-ci.org/AndyMacDroo/clusterf-chaos-proxy)

ClusterF** Chaos Proxy is an unreliable HTTP forward proxy; a lightweight tool designed for chaos testing of microservices. 

## Why Would I Need This?

I will let you in on a secret: **everything fails eventually**. Micro-services often communicate with other services via REST and HTTP. 
How does your micro-service cope when the services it depends on inevitably fail?

_You can find out how your service fairs with ClusterF***_.

## What Do I Do? (TLDR)

* Configure your locally running _service-under-test_ to point to ClusterF*** and configure ClusterF*** to point to your real running _dependent-destination-service_. 

* Switch on ClusterF*** and configure a "chaos strategy".

* Watch the world burn :fire: :fire: :fire:


## Building And Running ##

* Build project:
```sh
mvn clean install
```

* Create a `config` directory containing your `application.properties` and take note of the directory name.

* Run application - swapping `<LOCATION_OF_CONFIG>` for the directory from the step before:
```sh
java -jar clusterf-chaos-proxy*.jar -Dspring.config.location=<LOCATION_OF_CONFIG>/config/application.properties
```

## Configuration ##

Specify the port you would like to run **ClusterF***\** on:

```properties
server.port=8080 #8080 is the default
```

Use this information to configure your _service-under-test_ with relevant config for ClusterF*** in place of your _dependent-destination-service_.

### Example ###

**Configure Chaos Proxy**

Within ClusterF***, specify `application.properties` to point to your real destination service - e.g.:

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

Within your `application.properties`:

```properties
chaos.strategy=DELAY_RESPONSE
```

If you are specifying a delayed response - you can specify the number of seconds with:

```properties
chaos.strategy.delay_response.seconds=60
```

**Configure Service Under Test**

For example, you might configure your _service-under-test_ to point to a user service which has been _"ClusterF***'d"_.
If you configure your _service-under-test_ with properties files, they might change like so:

```properties
image.service=http://10.0.1.150:9898/user-service
```
To:
```properties
image.service=http://localhost:8080/user-service
```