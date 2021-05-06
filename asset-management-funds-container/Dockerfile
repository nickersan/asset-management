FROM adoptopenjdk:11.0.10_9-jdk-openj9-0.24.0

ARG IMAGE_DIR

RUN groupadd -r asset-management && useradd -r -g asset-management funds

RUN mkdir -p /opt/asset-management/funds
COPY ${IMAGE_DIR} /opt/asset-management/funds

RUN chown -R funds:asset-management /opt/asset-management/funds

#When building on Windows the directory permissions go wonky, seems adding rx permissions fixes this.
RUN chmod u+rx /opt/asset-management/funds/bin
RUN chmod u+rx /opt/asset-management/funds/lib
RUN chmod u+rx /opt/asset-management/funds/bin/start.sh

USER funds

ENTRYPOINT [ "bash", "/opt/asset-management/funds/bin/start.sh" ]