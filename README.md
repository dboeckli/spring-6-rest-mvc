# Spring Framework 6: Beginner to Guru
## Spring 6 Rest MVC API

This is the Backend Part. Application is listening on port 8081
* default profile: runs with a in memory h2 database: 
* localmysql: requires a local mysql installation

When Testing this module requires that the authentication server is up and running at localhost on port 9000. 

- openapi api-docs: http://localhost:8081/v3/api-docs
- openapi gui: http://localhost:8081/swagger-ui/index.html
- openapi-yaml: http://localhost:8081/v3/api-docs.yaml
- h2-console: http://localhost:8081/h2-console (check application.yaml for connection parameters)

## Docker 

### create image
```shell
.\mvnw clean package spring-boot:build-image
```
or just run 
```shell
.\mvnw clean install
```

## Kubernetes

### Generate Config Map for mysql init script

When updating 'src/scripts/init-mysql-mysql.sql', apply the changes to the Kubernetes ConfigMap:
```bash
kubectl create configmap mysql-init-script --from-file=init.sql=src/scripts/init-mysql.sql --dry-run=client -o yaml | Out-File -Encoding utf8 k8s/mysql-init-script-configmap.yaml
```

### Deployment with Kubernetes

Deployment goes into the default namespace.

To deploy all resources:
```bash
kubectl apply -f target/k8s/
```

To remove all resources:
```bash
kubectl delete -f target/k8s/
```

Check
```bash
kubectl get deployments -o wide
kubectl get pods -o wide
```

You can use the actuator rest call to verify via port 30081

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
