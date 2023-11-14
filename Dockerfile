FROM gutanbug53/taba4-backend-dev

WORKDIR /usr/app/

COPY build/libs/*.jar application.jar

EXPOSE 8080

ENTRYPOINT java -XX:+HeapDumpOnOutOfMemoryError -Duser.timezone="Asia/Seoul" -jar\
    application.jar\
    --spring.config.location=file:///usr/app/application.yml