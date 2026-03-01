# Notificacion Service - Sistema de Reservas de Hoteles

Microservicio de gestión de notificaciones y envío de emails. Consume eventos de Kafka y RabbitMQ para enviar notificaciones automáticas.

## Información del Servicio

| Propiedad | Valor |
|-----------|-------|
| Puerto | 8084 |
| Java | 21 |
| Spring Boot | 3.4.0 |
| Spring Cloud | 2024.0.1 |
| Context Path | /api/v1 |
| Base de Datos | MySQL |
| Message Broker | Kafka + RabbitMQ |

## Estructura del Proyecto

```
ms-notificacion/
├── pom.xml
├── Dockerfile
├── contracts/
│   └── notificacion-service-api.yaml
└── src/main/
    ├── java/com/hotel/notificacion/
    │   ├── NotificacionServiceApplication.java
    │   ├── api/
    │   │   ├── ContactoController.java
    │   │   ├── NotificacionesController.java
    │   │   └── PlantillasController.java
    │   ├── core/
    │   │   ├── notificacion/
    │   │   │   ├── model/Notificacion.java
    │   │   │   ├── repository/NotificacionRepository.java
    │   │   │   └── service/
    │   │   │       ├── NotificacionService.java
    │   │   │       ├── NotificacionListener.java
    │   │   │       └── ReservaNotificationKafkaListener.java
    │   │   └── plantilla/
    │   │       ├── model/Plantilla.java
    │   │       ├── repository/PlantillaRepository.java
    │   │       └── service/PlantillaService.java
    │   ├── helpers/ (exceptions, mappers)
    │   ├── infrastructure/ (config, security)
    │   └── internal/ (AuthInternalApi, events)
    └── resources/
        └── application.yml
```

## Endpoints

### Contacto (Público)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/contacto` | Enviar mensaje de contacto |

### Notificaciones (Admin)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/notificaciones` | Listar con filtros |
| GET | `/api/v1/notificaciones/{id}` | Obtener por ID |
| POST | `/api/v1/notificaciones/{id}/reenviar` | Reenviar fallida |
| POST | `/api/v1/notificaciones/enviar` | Envío manual |
| GET | `/api/v1/estadisticas` | Estadísticas |

### Mis Notificaciones (Usuario)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/mis-notificaciones` | Listar mis notificaciones |
| POST | `/api/v1/mis-notificaciones/{id}/leer` | Marcar como leída |
| POST | `/api/v1/mis-notificaciones/leer-todas` | Marcar todas leídas |

### Plantillas (Admin)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/plantillas` | Listar plantillas |
| POST | `/api/v1/plantillas` | Crear plantilla |
| GET | `/api/v1/plantillas/{id}` | Obtener por ID |
| PUT | `/api/v1/plantillas/{id}` | Actualizar |
| DELETE | `/api/v1/plantillas/{id}` | Eliminar |

## Variables de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `SERVER_PORT` | Puerto del servicio | `8084` |
| `SPRING_DATASOURCE_URL` | URL MySQL | `jdbc:mysql://mysql:3306/notificacion_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `hotel_user` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña BD | `hotel_pass` |
| `SPRING_RABBITMQ_HOST` | Host RabbitMQ | `rabbitmq` |
| `SPRING_RABBITMQ_PORT` | Puerto RabbitMQ | `5672` |
| `KAFKA_BOOTSTRAP_SERVERS` | Servidores Kafka | `kafka:29092` |
| `KAFKA_NOTIFICATION_GROUP_ID` | Group ID Kafka | `notificacion-service` |
| `MAIL_HOST` | Servidor SMTP | `smtp.gmail.com` |
| `MAIL_PORT` | Puerto SMTP | `587` |
| `MAIL_USERNAME` | Email remitente | `tu-email@gmail.com` |
| `MAIL_PASSWORD` | App Password | `xxxx-xxxx-xxxx` |
| `EUREKA_URL` | URL Eureka | `http://discovery-service:8761/eureka` |
| `AUTH_SERVICE_URL` | URL Auth Service | `http://auth-service:8081` |

---

## Docker

### Dockerfile

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
COPY contracts ./contracts
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder /app/target/*.jar app.jar
USER spring:spring
EXPOSE 8084
ENV JAVA_OPTS="-Xms256m -Xmx512m"
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8084/api/v1/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  notificacion-service:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: notificacion-service
    ports:
      - "8084:8084"
    environment:
      - SERVER_PORT=8084
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/notificacion_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=hotel_user
      - SPRING_DATASOURCE_PASSWORD=hotel_pass
      - SPRING_RABBITMQ_HOST=rabbitmq
      - SPRING_RABBITMQ_PORT=5672
      - SPRING_RABBITMQ_USERNAME=guest
      - SPRING_RABBITMQ_PASSWORD=guest
      - KAFKA_BOOTSTRAP_SERVERS=kafka:29092
      - KAFKA_NOTIFICATION_GROUP_ID=notificacion-service
      - SPRING_MAIL_HOST=smtp.gmail.com
      - SPRING_MAIL_PORT=587
      - SPRING_MAIL_USERNAME=${MAIL_USERNAME}
      - SPRING_MAIL_PASSWORD=${MAIL_PASSWORD}
      - EUREKA_URL=http://discovery-service:8761/eureka
      - CONFIG_SERVER_URL=http://config-server:8888
      - AUTH_SERVICE_URL=http://auth-service:8081
      - JAVA_OPTS=-Xms256m -Xmx512m
    depends_on:
      mysql:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
      kafka:
        condition: service_started
      auth-service:
        condition: service_healthy
    networks:
      - hotel-network
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8084/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
      start_period: 90s
    restart: unless-stopped

networks:
  hotel-network:
    external: true
```

### Comandos Docker

```bash
# Compilar
mvn clean package -DskipTests

# Construir imagen
docker build -t notificacion-service:latest .

# Ejecutar
docker run -d \
  --name notificacion-service \
  -p 8084:8084 \
  -e SERVER_PORT=8084 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/notificacion_db \
  -e SPRING_DATASOURCE_USERNAME=hotel_user \
  -e SPRING_DATASOURCE_PASSWORD=hotel_pass \
  -e KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
  -e SPRING_MAIL_USERNAME=tu-email@gmail.com \
  -e SPRING_MAIL_PASSWORD=tu-app-password \
  -e AUTH_SERVICE_URL=http://auth-service:8081 \
  -e EUREKA_URL=http://discovery-service:8761/eureka \
  --network hotel-network \
  notificacion-service:latest

# Verificar
curl http://localhost:8084/actuator/health
```

---

## Kubernetes

### Deployment

```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notificacion-service
  namespace: hotel-system
  labels:
    app: notificacion-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: notificacion-service
  template:
    metadata:
      labels:
        app: notificacion-service
    spec:
      containers:
        - name: notificacion-service
          image: ${ACR_NAME}.azurecr.io/notificacion-service:latest
          ports:
            - containerPort: 8084
          env:
            - name: SERVER_PORT
              value: "8084"
            - name: SPRING_DATASOURCE_URL
              value: "jdbc:mysql://mysql:3306/notificacion_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mysql-user
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mysql-password
            - name: SPRING_RABBITMQ_HOST
              value: "rabbitmq"
            - name: KAFKA_BOOTSTRAP_SERVERS
              value: "kafka:29092"
            - name: KAFKA_NOTIFICATION_GROUP_ID
              value: "notificacion-service"
            - name: SPRING_MAIL_HOST
              value: "smtp.gmail.com"
            - name: SPRING_MAIL_PORT
              value: "587"
            - name: SPRING_MAIL_USERNAME
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mail-username
            - name: SPRING_MAIL_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mail-password
            - name: EUREKA_URL
              value: "http://discovery-service:8761/eureka"
            - name: AUTH_SERVICE_URL
              value: "http://auth-service:8081"
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8084
            initialDelaySeconds: 90
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8084
            initialDelaySeconds: 60
            periodSeconds: 5
```

### Service

```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: notificacion-service
  namespace: hotel-system
spec:
  type: ClusterIP
  selector:
    app: notificacion-service
  ports:
    - port: 8084
      targetPort: 8084
      name: http
```

### Secret para Email

```yaml
# k8s/secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: notificacion-secrets
  namespace: hotel-system
type: Opaque
stringData:
  mail-username: "tu-email@gmail.com"
  mail-password: "tu-app-password"
```

### Comandos Kubernetes

```bash
# Aplicar manifiestos
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Verificar
kubectl get pods -n hotel-system -l app=notificacion-service
kubectl logs -f deployment/notificacion-service -n hotel-system

# Port-forward
kubectl port-forward svc/notificacion-service 8084:8084 -n hotel-system
```

---

## Azure

### 1. Construir y Subir a ACR

```bash
export ACR_NAME="acrhotelreservas"

az acr login --name $ACR_NAME

az acr build \
  --registry $ACR_NAME \
  --image notificacion-service:v1.0.0 \
  --image notificacion-service:latest \
  .
```

### 2. Deployment en AKS

```yaml
# k8s/azure-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notificacion-service
  namespace: hotel-system
spec:
  replicas: 2
  selector:
    matchLabels:
      app: notificacion-service
  template:
    metadata:
      labels:
        app: notificacion-service
    spec:
      containers:
        - name: notificacion-service
          image: acrhotelreservas.azurecr.io/notificacion-service:v1.0.0
          ports:
            - containerPort: 8084
          env:
            - name: SERVER_PORT
              value: "8084"
            - name: SPRING_DATASOURCE_URL
              value: "jdbc:mysql://mysql-hotel-reservas.mysql.database.azure.com:3306/notificacion_db?useSSL=true"
            - name: SPRING_DATASOURCE_USERNAME
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mysql-user
            - name: SPRING_DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mysql-password
            - name: KAFKA_BOOTSTRAP_SERVERS
              value: "eh-hotel-reservas.servicebus.windows.net:9093"
            - name: SPRING_MAIL_USERNAME
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mail-username
            - name: SPRING_MAIL_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: hotel-secrets
                  key: mail-password
            - name: EUREKA_URL
              value: "http://discovery-service:8761/eureka"
            - name: AUTH_SERVICE_URL
              value: "http://auth-service:8081"
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1Gi"
              cpu: "500m"
```

### 3. Azure DevOps Pipeline

```yaml
# azure-pipelines.yml
trigger:
  branches:
    include:
      - main
  paths:
    include:
      - ms-notificacion/**

variables:
  dockerRegistryServiceConnection: 'acr-connection'
  imageRepository: 'notificacion-service'
  containerRegistry: 'acrhotelreservas.azurecr.io'
  dockerfilePath: 'ms-notificacion/Dockerfile'
  tag: '$(Build.BuildId)'

pool:
  vmImage: 'ubuntu-latest'

stages:
  - stage: Build
    jobs:
      - job: Build
        steps:
          - task: Maven@3
            displayName: 'Maven Package'
            inputs:
              mavenPomFile: 'ms-notificacion/pom.xml'
              goals: 'clean package'
              options: '-DskipTests'
              javaHomeOption: 'JDKVersion'
              jdkVersionOption: '1.21'

          - task: Docker@2
            displayName: 'Build and Push'
            inputs:
              command: buildAndPush
              repository: $(imageRepository)
              dockerfile: $(dockerfilePath)
              containerRegistry: $(dockerRegistryServiceConnection)
              tags: |
                $(tag)
                latest

  - stage: Deploy
    dependsOn: Build
    jobs:
      - deployment: Deploy
        environment: 'production'
        strategy:
          runOnce:
            deploy:
              steps:
                - task: KubernetesManifest@0
                  inputs:
                    action: deploy
                    kubernetesServiceConnection: 'aks-connection'
                    namespace: hotel-system
                    manifests: |
                      ms-notificacion/k8s/*.yaml
                    containers: |
                      $(containerRegistry)/$(imageRepository):$(tag)
```

---

## Eventos Consumidos

### Kafka (Topic: reserva.notifications)

| Evento | Trigger | Acción |
|--------|---------|--------|
| CREATED | Nueva reserva | Email confirmación |
| CONFIRMED | Pago confirmado | Email confirmación pago |
| CANCELLED | Usuario cancela | Email cancelación |
| CANCELLED_ADMIN | Admin cancela | Email cancelación admin |

### RabbitMQ (Exchange: hotel.events)

| Routing Key | Evento | Acción |
|-------------|--------|--------|
| `reserva.created` | ReservaCreatedEvent | Email nueva reserva |
| `reserva.confirmed` | ReservaConfirmedEvent | Email confirmación |
| `reserva.cancelled` | ReservaCancelledEvent | Email cancelación |
| `user.registered` | UserRegisteredEvent | Email bienvenida |
| `user.login` | UserLoginEvent | Email bienvenida |

---

## Modelo de Datos

### Notificacion

```java
@Entity
public class Notificacion {
    Long id;
    String tipo;           // EMAIL, SMS, PUSH
    String destinatario;
    String asunto;
    String contenido;
    String estado;         // PENDIENTE, ENVIADA, FALLIDA
    LocalDateTime fechaCreacion;
    LocalDateTime fechaEnvio;
    Integer intentos;
    String errorMensaje;
    Long userId;
    Boolean leida;
}
```

### Plantilla

```java
@Entity
public class Plantilla {
    Long id;
    String codigo;         // RESERVA_CONFIRMADA, etc.
    String nombre;
    String asunto;
    String contenido;      // Soporta {{variable}}
    String tipo;
    Boolean activa;
}
```

---

## Configuración Gmail SMTP

Para usar Gmail como servidor SMTP:

1. Habilita "Acceso de aplicaciones menos seguras" o
2. Genera un App Password:
   - Ve a Google Account > Security
   - Activa 2-Step Verification
   - Genera App Password para "Mail"
   - Usa ese password en `MAIL_PASSWORD`

---

## Troubleshooting

```bash
# Ver logs
kubectl logs -f deployment/notificacion-service -n hotel-system

# Verificar Kafka consumer
kubectl exec -it deployment/notificacion-service -n hotel-system -- \
  wget -qO- http://localhost:8084/actuator/health

# Verificar RabbitMQ
kubectl exec -it deployment/notificacion-service -n hotel-system -- \
  wget -qO- http://localhost:8084/actuator/health | grep rabbitmq

# Test envío manual
curl -X POST http://localhost:8084/api/v1/notificaciones/enviar \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"tipo":"EMAIL","destinatario":"test@test.com","asunto":"Test","contenido":"Prueba"}'
```

---

## Ejecución Local

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/notificacion-service-1.0.0-SNAPSHOT.jar \
  --server.port=8084 \
  --spring.datasource.url=jdbc:mysql://localhost:3306/notificacion_db \
  --spring.mail.username=tu-email@gmail.com \
  --spring.mail.password=tu-app-password

# Swagger UI
open http://localhost:8084/api/v1/swagger-ui.html
```
