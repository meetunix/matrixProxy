# matrixProxy

A small web service that acts as a proxy for a Matrix homeserver. It receives a simple message and
does a retransmit to a Matrix homeserver. Very useful for IoT devices who cant handle the Matrix
api due to ressource restrictions.

The state of the project is in alpha but its working.

## prerequisites

1. You need a running Matrix homerserver
2. On this homeserver you need a user with valid password (other login types are not supported)
3. There must be a room where the user is member of or the user can join

## installation

Clone the repository and use Apache Maven to compile the project.

    mvn clean compile package
    
Copy the single jar-file to a location of your choice

    cp target/matrixProxy-0.1.0-jar-with-dependencies.jar ../matrixProxy.jar
    

## configure

matrixProxy uses a configuration file. `sample_configuration.conf` is an example configuration
file.

    BASE_URL=http://localhost:7654/matrix/
    HOMESERVER_URL=https://example.com:8448
    HOMESERVER_USER=username
    HOMESERVER_PASS=secret

Substitute the values and save the file.

    cp sample_configuration.conf /PATH/TO/CONFIG/matrixProxy.conf


## run

    java -jar matrixProxy.jar -c /PATH/TO/CONFIG/matrixProxy.conf
    
matrixProxy is going to write some log messages to `/tmp/MatrixProxy0.log`.
    

## use

Simply send the json-string `{"message": "ring ring", "room": "#doorbell"}` to the matrixProxy
via `HTTP-POST`. The Message will be retransmitted to the Matrix homeserver. Is the previously
configured user is not member of the given room, it will be added.

    curl -i -H "Content-Type: application/json" -XPOST  http://localhost:7654/matrix/transmit/message --data-binary '{"message": "Hallo123", "room": "#klingel"}'


## TODO

* better logging
* testing
* caching messages when the server is not reachable





