#!/bin/bash
set -x
docker exec -ti kafka 'bash' -c '$KAFKA_HOME/bin/kafka-topics.sh --zookeeper ${$KAFKA_ZOOKEEPER_CONNECT} --alter --topic inventory_service_vehicles --partitions 5' $@
