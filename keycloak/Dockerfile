FROM quay.io/keycloak/keycloak:26.0 AS builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=false
ENV KC_FEATURES=token-exchange
ENV KC_BOOTSTRAP_ADMIN_USERNAME=keycloak
ENV KC_BOOTSTRAP_ADMIN_PASSWORD=keycloak
ENV KC_DB=dev-file
# Import the realm
ADD one-citations-realm.json /opt/keycloak/data/import/
RUN /opt/keycloak/bin/kc.sh import --file /opt/keycloak/data/import/one-citations-realm.json

RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:26.0
COPY --from=builder /opt/keycloak/ /opt/keycloak/
WORKDIR /opt/keycloak

ENTRYPOINT [ "/opt/keycloak/bin/kc.sh" ]

CMD ["start-dev"]

HEALTHCHECK --interval=2s CMD curl http://localhost:8089/realms/tindeer