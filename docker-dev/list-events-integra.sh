#!/bin/bash
set -x
ssh 10.44.216.79 -- bash -cl '"/opt/kafka/bin/kafka-console-consumer.sh --zookeeper statbroker44-1 --topic inventory_service_vehicles --from-beginning"'
