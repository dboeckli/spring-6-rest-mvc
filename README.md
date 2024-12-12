# Spring Framework 6: Beginner to Guru
## Spring 6 Rest MVC API

This is the Backend Part. Application is listening on port 8081
* default profile: runs with a in memory h2 database: 
* localmysql: requires a local mysql installation

When Testing this module requires that the authentication server is up and running at localhost on port 9000. 

openapi api-docs: http://localhost:8081/v3/api-docs
openapi gui: http://localhost:8081/swagger-ui/index.html
openapi-yaml: http://localhost:8081/v3/api-docs.yaml
h2-console: http://localhost:8081/h2-console (check application.yaml for connection parameters)

## Docker 

### create image
```shell
.\mvnw clean package spring-boot:build-image
```
or just run 
```shell
.\mvnw clean install
```

### run image

Hint: remove the daemon flag -d to see what is happening, else it run in background

```shell
docker run --name mysql -d -e MYSQL_USER=restadmin -e MYSQL_PASSWORD=password -e MYSQL_DATABASE=restdb -e MYSQL_ROOT_PASSWORD=password mysql:9
docker stop mysql
docker rm mysql
docker start mysql

docker run --name rest-mvc -d -p 8081:8080 -e SPRING_PROFILES_ACTIVE=localmysql -e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://auth-server:9000 -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/restdb -e SERVER_PORT=8080 --link auth-server:auth-server --link mysql:mysql spring-6-rest-mvc:0.0.1-SNAPSHOT
 
docker stop rest-mvc
docker rm rest-mvc
docker start rest-mvc
```

Chapter 1-17

This repository is for an example application built in my [Spring Framework 6 - Beginner to Guru](https://www.udemy.com/course/spring-framework-6-beginner-to-guru/?referralCode=2BD0B7B7B6B511D699A9) online course

The application is a simple Spring Boot 3 / Spring Framework 6 web application. It is used to help students learn how
to use the Spring Framework. Step by step instructions and detailed explanations can be found within the course.

As you work through the course, please feel free to fork this repository to your out GitHub repo. Most links contain links
to source code changes. If you encounter a problem you can compare your code to the lesson code. [See this link for help with compares](https://github.com/springframeworkguru/spring5webapp/wiki#getting-an-error-but-cannot-find-what-is-different-from-lesson-source-code)

## Spring Framework 6: Beginner to Guru Course Wiki
Got a question about your Spring Framework 6 course? [Checkout these FAQs!](https://github.com/springframeworkguru/spring5webapp/wiki)

## Getting Your Development Environment Setup
### Recommended Versions
| Recommended             | Reference                                                                                                                                                     | Notes                                                                                                                                                                                                                  |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Oracle Java 21 JDK      | [Download](https://www.oracle.com/java/technologies/downloads/#java21) | Java 17 or higher is required for Spring Framework 6. Java 21 is recommended for the course.                                                                                                                           |
| IntelliJ 2024 or Higher | [Download](https://www.jetbrains.com/idea/download/)                                                                                                          | Ultimate Edition recommended. Students can get a free 120 trial license [here](https://github.com/springframeworkguru/spring5webapp/wiki/Which-IDE-to-Use%3F#how-do-i-get-the-free-120-day-trial-to-intellij-ultimate) |
| Maven 3.9.6 or higher   | [Download](https://maven.apache.org/download.cgi)                                                                                                             | [Installation Instructions](https://maven.apache.org/install.html)                                                                                                                                                     |
| Gradle 8.7 or higher    | [Download](https://gradle.org/install/)                                                                                                                       |                                                                                                                                                                     |
| Git 2.39 or higher      | [Download](https://git-scm.com/downloads)                                                                                                                     |                                                                                                                                                                                                                        | 
| Git GUI Clients         | [Downloads](https://git-scm.com/downloads/guis)                                                                                                               | Not required. But can be helpful if new to Git. SourceTree is a good option for Mac and Windows users.                                                                                                                 |

## All Spring Framework Guru Courses
### Spring Framework 6
* [Spring Framework 6 - Beginner to Guru](https://www.udemy.com/course/spring-framework-6-beginner-to-guru/?referralCode=2BD0B7B7B6B511D699A9)
* [Spring AI: Beginner to Guru](https://www.udemy.com/course/spring-ai-beginner-to-guru/?referralCode=EF8DB31C723FFC8E2751)
* [Hibernate and Spring Data JPA: Beginner to Guru](https://www.udemy.com/course/hibernate-and-spring-data-jpa-beginner-to-guru/?referralCode=251C4C865302C7B1BB8F)
* [API First Engineering with Spring Boot](https://www.udemy.com/course/api-first-engineering-with-spring-boot/?referralCode=C6DAEE7338215A2CF276)
* [Introduction to Kafka with Spring Boot](https://www.udemy.com/course/introduction-to-kafka-with-spring-boot/?referralCode=15118530CA63AD1AF16D)
* [Spring Security: Beginner to Guru](https://www.udemy.com/course/spring-security-core-beginner-to-guru/?referralCode=306F288EB78688C0F3BC)

