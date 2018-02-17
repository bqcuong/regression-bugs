# Pallmall Spring Boot Playground

## Build


```
./mvnw install
```

## Run

And then launch the server:

```
java -jar target/pallmall-0.0.1-SNAPSHOT.jar
```

And visit http://localhost:8080/ in your browser.

You can also build and run a docker container, see below.

## Docker

See [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/).  Basically:

```
./mvnw install dockerfile:build
```

And then:

```
docker run -d -p 8080:8080 cscie599/pallmall
```

Alternatively (to not keep the container running after ctrl-c):
```
docker run --rm -it -p 8080:8080 cscie599/pallmall
```
