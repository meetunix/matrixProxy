FROM  maven:3-amazoncorretto-21 AS builder
RUN mkdir /matrixProxy
WORKDIR /matrixProxy
COPY pom.xml /matrixProxy/
RUN mvn dependency:resolve
COPY src /matrixProxy/src
RUN mvn compile package && mv /matrixProxy/target/*dependencies.jar /matrixProxy/matrixProxy.jar

FROM amazoncorretto:21-alpine
COPY --from=builder /matrixProxy/matrixProxy.jar /matrixProxy.jar
EXPOSE 7654
CMD ["java", "-jar", "matrixProxy.jar"]
