FROM nginx:alpine
MAINTAINER dpostoronc
RUN apk update && apk upgrade && apk add curl
COPY vehicles_dump.sh /
ENV INTEGRA 216

RUN chmod +x /vehicles_dump.sh && /vehicles_dump.sh
