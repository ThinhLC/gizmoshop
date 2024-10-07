FROM ubuntu:latest
LABEL authors="thinh"

ENTRYPOINT ["top", "-b"]