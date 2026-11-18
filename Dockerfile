FROM  maven:3-amazoncorretto-21 AS builder
RUN mkdir /matrixProxy
WORKDIR /matrixProxy
COPY pom.xml /matrixProxy/
RUN mvn dependency:resolve
COPY src /matrixProxy/src
RUN mvn compile package && mv /matrixProxy/target/*dependencies.jar /matrixProxy/matrixProxy.jar

# CVE-2026-114938 ("Java4Shell") is a critical RCE in the AVX-512 accelerated
# Base64 decoder of OpenJDK 17.0.6+/21/25/27. It is fixed in Corretto 21.0.13.1.2
# (Amazon Corretto November 2026 CSPU), so pin the runtime image to that release
# instead of the floating 21-alpine tag.
FROM amazoncorretto:21.0.13-alpine
# Refuse to build if the base image does not contain the fixed Corretto build.
RUN java -version 2>&1 | grep -qF '21.0.13.1' \
      || { echo 'FATAL: base image JVM lacks the CVE-2026-114938 (Java4Shell) fix, expected Corretto 21.0.13.1.2' >&2; exit 1; }
COPY --from=builder /matrixProxy/matrixProxy.jar /matrixProxy.jar
EXPOSE 7654
CMD ["java", "-jar", "matrixProxy.jar"]
