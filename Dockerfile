FROM maven:3-openjdk-11
RUN mkdir /matrixProxy
WORKDIR /matrixProxy
# build the app
COPY src /matrixProxy/src
COPY pom.xml /matrixProxy/
RUN mvn compile package && mv /matrixProxy/target/*dependencies.jar /matrixProxy/matrixProxy.jar

EXPOSE 7654

CMD ["java", "-jar", "matrixProxy.jar", "-c", "/etc/matrixProxy/docker.conf"]
