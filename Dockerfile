FROM registry.gitlab.com/code_smith/pitech/core/pitech/pitech-base-alpine

MAINTAINER "Codesmith"

WORKDIR /app

COPY build/libs/loan-api-0.0.1-RELEASE.jar loan-api.jar

COPY src/main/resources/1cb/secure2.1cb.kz.crt ca-cert-secure2.1cb.kz.crt
COPY src/main/resources/1cb/test1.1cb.kz.crt ca-cert-test1.1cb.kz.crt
COPY src/main/resources/1cb/secure2.1cb.kz.2022.crt ca-cert-secure2.1cb.kz.2022.crt
COPY src/main/resources/1cb/test2.1cb.kz.crt ca-cert-test2.1cb.kz.crt
RUN $JAVA_HOME/bin/keytool -import -noprompt -trustcacerts -alias secure2.1cb.kz -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -file ca-cert-secure2.1cb.kz.crt
RUN $JAVA_HOME/bin/keytool -import -noprompt -trustcacerts -alias test1.1cb.kz -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -file ca-cert-test1.1cb.kz.crt
RUN $JAVA_HOME/bin/keytool -import -noprompt -trustcacerts -alias secure2.1cb.kz.2022 -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -file ca-cert-secure2.1cb.kz.2022.crt
RUN $JAVA_HOME/bin/keytool -import -noprompt -trustcacerts -alias test2.1cb.kz -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -file ca-cert-test2.1cb.kz.crt

EXPOSE 8083

ENTRYPOINT ["java"]

RUN apk add --update ttf-dejavu &&  apk add --update fontconfig

CMD [ "-XX:+UnlockExperimentalVMOptions", \
      "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/loan-api.jar" ]
