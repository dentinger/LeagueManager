FROM docker.kroger.com/library/alpine-java

WORKDIR /opt
COPY build/libs/neo4j-demo*jar /opt/neo4j-demo.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "neo4j-demo.jar", "nodeFirst", "loadAll"]

