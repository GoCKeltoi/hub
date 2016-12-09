#!/bin/bash
set -x
docker exec kafka 'sh' -c '$KAFKA_HOME/bin/kafka-console-consumer.sh --zookeeper ${KAFKA_ZOOKEEPER_CONNECT} --topic inventory_service_vehicles --from-beginning'
