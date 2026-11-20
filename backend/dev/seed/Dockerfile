FROM alpine:3.22

RUN apk add --no-cache postgresql17-client curl zip imagemagick imagemagick-jpeg font-dejavu redis \
    && adduser -D seed

USER seed

ENTRYPOINT ["sh", "/seed/run.sh"]
