FROM registry.corp.mobile.de/mobile-java8
MAINTAINER Board Runners
ADD build/libs/inventory-list-indexer.jar /opt/inventory-list-indexer-v2.jar

# Expose the port of the service
EXPOSE 8080

# Launch the vehicle-service
ENTRYPOINT ["java", "-jar", "/opt/inventory-list-indexer-v2.jar"]
