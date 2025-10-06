#Build
FROM maven:3.9.11-amazoncorretto-21-al2023 as build
WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

#Run
FROM amazoncorretto:21.0.5
WORKDIR /app

COPY --from=build ./build/target/*.jar ./emprestimo.jar

EXPOSE 8080

ENV DATASOURCE_URL=''
ENV DATASOURCE-USERNAME=''
ENV DATASOURCE_PASSWORD=''
ENV JWT_SECRET=''

ENV TZ='America/Brasilia'

ENTRYPOINT java -jar emprestimo.jar