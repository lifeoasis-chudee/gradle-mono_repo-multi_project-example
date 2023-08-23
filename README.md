# gradle-mono_repo-multi_project-example

이 프로젝트는 gradle multi module project 를 만드는 예제입니다.

프로젝트 환경은 다음과 같습니다.

- [grpc-spring-boot-starter](https://yidongnan.github.io/grpc-spring-boot-starter/en/)을 이용한 grpc 도메인 모듈이 있습니다.
- 해당 grpc 모듈을 사용하는 BFF 어플리케이션이 있습니다 ([spring-boot-starter-graphql](https://docs.spring.io/spring-graphql/docs/current/reference/html/))

이 외에 kotlin 언어를 사용하였고 gradle 빌드언어인 [kotlin DSL](https://blog.jetbrains.com/ko/kotlin/2023/05/kotlin-dsl-is-the-default-for-new-gradle-builds/)을 사용하였습니다.


## Getting started

1. 로컬 환경에 각 모듈을 [jib](https://cloud.google.com/java/getting-started/jib?hl=ko)로 자바 컨테이너를 빌드합니다.
```shell
./gradlew :user:jibDockerBuild
./gradlew :graphql:jibDockerBuild
```

2. docker compose 로 도커 컨테이너를 실행시킵니다.
```shell
docker compose up -d
```

3. user grpc 메서드를 테스트해봅니다. [grpcurl](https://github.com/fullstorydev/grpcurl)
```shell
grpcurl --plaintext localhost:9090 list 
grpcurl --plaintext localhost:9090 list user.UserService
grpcurl --plaintext -d "{\"user\": {\"name\": \"Test\", \"age\": 30}}" localhost:9090 user.UserService.CreateUser
grpcurl --plaintext -d "{\"name\": \"Test\"}" localhost:9090 user.UserService.GetUser
```

4. graphql 어플리케이션에 접속하여 user 도메인의 데이터를 생성하거나 불러옵니다.
