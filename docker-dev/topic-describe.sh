#!/bin/bash
set -x
docker-compose run kafka 'bash' -c '$KAFKA_HOME/bin/kafka-topics.sh --zookeeper $KAFKA_ZOOKEEPER_CONNECT --describe --topic inventory_service_vehicles' $@
