#!/bin/bash
set -x
cat delete-event.json | tr -d '\n' | docker exec -i kafka 'sh' -c '$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic inventory_service_vehicles'
