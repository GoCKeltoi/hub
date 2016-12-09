#!/bin/sh
if [ -z "$INTEGRA" ]
then
  echo "Unable to dump vehicles from integra. INTEGRA variable is not set." >&2
  exit 1
fi

echo "Downloading vehicles dump from integra: $INTEGRA"
curl -x "10.44.$INTEGRA.99:80" http://vehicle-service.service.consul/vehicles > /usr/share/nginx/html/vehicles
