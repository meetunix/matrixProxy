# matrixProxy

A small web service that acts as a proxy for a Matrix homeserver. It receives a simple message and
does retransmit it to a Matrix homeserver. Handy for IoT devices who can't handle the Matrix
api due to resource restrictions.

## prerequisites

1. You need a running Matrix homeserver
2. On this homeserver you need a user with a valid password (other login types are not supported)
3. There must be a room where the user is a member of or the user can join
4. The room must be listed (e.g. `#doorbell:example.com`) in the local directory of the homeserver
(can be done while creating the room ot in the room configurations)


## Docker

    docker build -t matrix-proxy .
    docker run --rm -d -p 0.0.0.0:7654:7654 -e BASE_URL="0.0.0.0:7654" -e HOMESERVER_URL="<YOUR-HOMESERVER>" -e HOMESERVER_USER="<USERNAME>" -e HOMESERVER_PASS="<PASSWORD>" matrix-proxy

## Without docker

Clone the repository and use Apache Maven to compile the project.

    mvn clean compile package

Copy the single jar-file to a location of your choice

    cp target/matrixProxy-*-jar-with-dependencies.jar ../matrixProxy.jar

### configure

matrixProxy uses a configuration file. `sample_configuration.conf` is an example configuration
file.

    BASE_URL=http://localhost:7654/matrix/
    HOMESERVER_URL=https://example.com:8448
    HOMESERVER_USER=username
    HOMESERVER_PASS=secret

Substitute the values and save the file.

    cp sample_configuration.conf /PATH/TO/CONFIG/matrixProxy.conf

### run

    java -jar matrixProxy.jar -c /PATH/TO/CONFIG/matrixProxy.conf


## Send Messages

Simply send the json-string `{"message": "ring ring", "room": "#doorbell"}` to the matrixProxy
via `HTTP-POST`. The Message will be retransmitted to the Matrix homeserver. If the previously
configured user is not a member of the given room, it will be added. The room must be available in the
local directory of your homeserver (`#doorbell:example.com`), otherwise the proxy can't find the
room.

    curl -i -H "Content-Type: application/json" -XPOST  http://localhost:7654/matrix/transmit/message --data-binary '{"message": "Hallo123", "room": "#doorbell"}'


## TODO

* message formatting (markdown)
* caching messages when the server is not reachable
