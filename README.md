# Notificacion Service - Sistema de Reserva de Hoteles

Microservicio de gestión de notificaciones y envío de correos. **Doble consumidor**: Kafka para eventos de reservas y RabbitMQ para eventos de usuarios y mensajes de contacto. Renderiza templates Thymeleaf y envía vía SMTP usando la librería externa `notification_lib` (JitPack).

## Información del Servicio

| Propiedad | Valor |
|-----------|-------|
| Puerto | 8084 |
| Java | 21 |
| Spring Boot | 3.4.0 |
| Spring Cloud | 2024.0.1 |
| Context Path | `/api/v1` |
| Base de Datos | MySQL |
| Mensajería | **Kafka** (consumer) + **RabbitMQ** (consumer) |
| Email | SMTP via `notification_lib` v1.1.0 (JitPack) |
| Validación JWT | **RS256** con clave pública RSA compartida |

## Estructura del Proyecto

```
ms-notificacion-service/
├── pom.xml
├── Dockerfile
├── env.example                ← copialo a .env y completalo en DEV
├── contracts/
│   └── notificacion-service-api.yaml
└── src/main/
    ├── java/com/hotel/notificacion/
    │   ├── NotificacionServiceApplication.java
    │   ├── api/ (ContactoController, NotificacionesController, PlantillasController)
    │   ├── core/
    │   │   ├── notificacion/
    │   │   │   ├── model, repository
    │   │   │   └── service/
    │   │   │       ├── NotificacionService.java
    │   │   │       ├── NotificacionListener.java         ← RabbitMQ user events
    │   │   │       ├── ContactoListener.java             ← RabbitMQ contacto
    │   │   │       └── ReservaNotificationKafkaListener.java  ← Kafka reservas
    │   │   └── plantilla/ (model, repository, service)
    │   ├── helpers/ (mappers, exceptions)
    │   ├── infrastructure/
    │   │   ├── config/ (SecurityConfig, JwtConfig, RabbitConfig, EmailLibConfig, RestTemplateConfig)
    │   │   └── security/ (AuthContextFilter, AuthUtils)
    │   └── internal/ (events, dto)
    └── resources/
        ├── application.yml    ← bootstrap mínimo (config-server lo hidrata)
        └── templates/
            ├── welcome-email.html
            ├── password-reset-email.html
            ├── contacto-soporte-email.html
            ├── reserva-created-email.html
            ├── reserva-confirmed-email.html
            └── reserva-cancelled-email.html
```

## Endpoints

### Contacto (público)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/contacto` | Enviar mensaje de contacto desde el frontend |

### Notificaciones (admin)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/notificaciones` | Listar con filtros |
| GET | `/api/v1/notificaciones/{id}` | Obtener por ID |
| POST | `/api/v1/notificaciones/{id}/reenviar` | Reenviar notificación fallida |
| POST | `/api/v1/notificaciones/enviar` | Envío manual |
| GET | `/api/v1/estadisticas` | Estadísticas |

### Mis Notificaciones (usuario autenticado)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/mis-notificaciones` | Listar mis notificaciones |
| POST | `/api/v1/mis-notificaciones/{id}/leer` | Marcar como leída |
| POST | `/api/v1/mis-notificaciones/leer-todas` | Marcar todas leídas |

### Plantillas (admin)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/plantillas` | Listar plantillas |
| POST | `/api/v1/plantillas` | Crear plantilla |
| GET | `/api/v1/plantillas/{id}` | Obtener por ID |
| PUT | `/api/v1/plantillas/{id}` | Actualizar |
| DELETE | `/api/v1/plantillas/{id}` | Eliminar |

## Variables de Entorno

| Variable | Obligatoria | Descripción | Ejemplo (DEV) |
|----------|-------------|-------------|---------------|
| `CONFIG_IMPORT` | No | Import de Spring Cloud Config | `optional:configserver:http://localhost:8888` |
| `CONFIG_FAIL_FAST` | No | Falla rápido si config-server no responde | `false` (DEV) / `true` (PROD) |
| `SERVER_PORT` | No | Puerto HTTP (default 8084) | `8084` |
| `EUREKA_URL` | No | URL de Eureka (default `http://discovery-service:8761/eureka`) | `http://localhost:8761/eureka` |
| `SPRING_DATASOURCE_URL` | **Sí** | JDBC URL MySQL | `jdbc:mysql://localhost:3307/notificacion_db` |
| `SPRING_DATASOURCE_USERNAME` | **Sí** | Usuario MySQL | - |
| `SPRING_DATASOURCE_PASSWORD` | **Sí** | Contraseña MySQL | - |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | No | Default `validate` (PROD-safe) | `update` (DEV) |
| `SPRING_JPA_SHOW_SQL` | No | Default `false` (PROD-safe) | `true` (DEV) |
| `SPRING_RABBITMQ_HOST` | **Sí** | Host RabbitMQ | `localhost` |
| `SPRING_RABBITMQ_PORT` | **Sí** | Puerto RabbitMQ | `5672` |
| `SPRING_RABBITMQ_USERNAME` | **Sí** | Usuario RabbitMQ | - |
| `SPRING_RABBITMQ_PASSWORD` | **Sí** | Contraseña RabbitMQ | - |
| `KAFKA_BOOTSTRAP_SERVERS` | **Sí** | Brokers Kafka | `localhost:9092` |
| `KAFKA_NOTIFICATION_GROUP_ID` | No | Default `notificacion-service` | `notificacion-service` |
| `KAFKA_RESERVA_NOTIFICATIONS_TOPIC` | No | Default `reserva.notifications` | - |
| `JWT_PUBLIC_KEY` | **Sí** | Clave pública RSA del auth-service (PEM 1 línea con `\n`) | - |
| `AUTH_SERVICE_URL` | **Sí** | Base URL del auth-service (sin context-path) | `http://localhost:8081` |
| `AUTH_SERVICE_CLIENT_ID` | **Sí** | Client ID asignado a notificacion-service en auth | - |
| `AUTH_SERVICE_CLIENT_SECRET` | **Sí** | Client secret correspondiente | - |
| `MAIL_HOST` | No | Host SMTP (default `smtp.gmail.com`) | `smtp.gmail.com` |
| `MAIL_PORT` | No | Puerto SMTP (default 587) | `587` |
| `MAIL_USERNAME` | **Sí** | Usuario SMTP | - |
| `MAIL_PASSWORD` | **Sí** | Contraseña SMTP (App Password en Gmail) | - |
| `MAIL_FROM_EMAIL` | No | Email remitente (default = `MAIL_USERNAME`) | - |
| `MAIL_FROM_NAME` | No | Default `LuxeStay` | `LuxeStay` |
| `MAIL_STARTTLS` | No | Default `true` | `true` |
| `SUPPORT_EMAIL` | **Sí** | Destinatario de mensajes de contacto | - |
| `CORS_ALLOWED_ORIGINS` | **Sí** | Origen permitido para CORS | `http://localhost:4200` |

> Las credenciales `AUTH_SERVICE_CLIENT_ID/SECRET` deben coincidir con `NOTIFICACION_SERVICE_CLIENT_ID/SECRET` sembradas en `auth-service`.

## Eventos Consumidos

### Kafka — topic `reserva.notifications`

Consumer group: `notificacion-service`. Listener: `ReservaNotificationKafkaListener`.

| `eventType` | Acción | Template |
|-------------|--------|----------|
| `CREATED` (normalizado a `PENDING`) | Email reserva creada | `reserva-created-email.html` |
| `CONFIRMED` | Email reserva confirmada | `reserva-confirmed-email.html` |
| `CANCELLED` / `CANCELLED_ADMIN` | Email reserva cancelada | `reserva-cancelled-email.html` |

Activado por flag de config: `app.notifications.reserva.enabled` (default `true`).

### RabbitMQ — exchange `hotel.events` (TopicExchange)

Listener: `NotificacionListener` sobre la queue `notification.events.queue`.

| Routing Key | Evento | Acción |
|-------------|--------|--------|
| `user.registered` | `UserRegisteredEvent` | Email de bienvenida (`welcome-email.html`) |
| `user.login` | `UserLoginEvent` | Email simple "bienvenido nuevamente" (omitido para ADMIN) |
| `user.password.reset` | `PasswordResetEvent` | Email con código (`password-reset-email.html`) |

Activado por flag: `app.notifications.sesion.enabled` (default `true`).

### RabbitMQ — exchange/queue de contacto (configurable)

Listener: `ContactoListener` sobre la queue configurada por `app.rabbitmq.contacto.queue`.

| Evento | Acción |
|--------|--------|
| `ContactoEvent` | Persiste el mensaje y dispara email a `SUPPORT_EMAIL` (`contacto-soporte-email.html`) |

Activado por flag: `app.notifications.contacto.enabled` (default `true`).

### Cross-package type mapping

`RabbitConfig.java:97-112` define el mapping entre los eventos publicados por `auth-service` (`com.hotel.auth.infrastructure.events.*`) y los locales (`com.hotel.notificacion.internal.events.*`). Resuelve la divergencia de package paths entre publisher y consumer.

## Templates Thymeleaf

Cada listener resuelve el template apropiado y le pasa un `Map<String, String>` con variables. Los templates están en `src/main/resources/templates/`:

- `welcome-email.html`
- `password-reset-email.html`
- `contacto-soporte-email.html`
- `reserva-created-email.html`
- `reserva-confirmed-email.html`
- `reserva-cancelled-email.html`

## Modelo de Datos

### Notificacion

```java
@Entity
public class Notificacion {
    Long id;
    String tipo;            // EMAIL, LOGIN, ...
    String destinatario;
    String asunto;
    String contenido;
    String estado;          // PENDIENTE, ENVIADA, FALLIDA
    LocalDateTime fechaCreacion;
    LocalDateTime fechaEnvio;
    Integer intentos;
    String errorMensaje;
    Long userId;
    Boolean leida;
    String eventType;
}
```

### Plantilla

```java
@Entity
public class Plantilla {
    Long id;
    String codigo;          // RESERVA_CONFIRMADA, etc.
    String nombre;
    String asunto;
    String contenido;       // soporta {{variable}}
    String tipo;
    Boolean activa;
}
```

## Seguridad

- **Validación JWT**: RS256 con `JWT_PUBLIC_KEY`.
- **Sesiones**: STATELESS.
- **CORS**: deshabilitado en el servicio (lo maneja el `api-gateway`).
- **Rutas públicas**: `/actuator/health`, `POST /contacto/**`. El resto requiere JWT.

## Configuración Gmail SMTP

1. Habilitar 2-Step Verification en la cuenta de Google.
2. Generar App Password (Security → App passwords → Mail).
3. Setear `MAIL_USERNAME` con el email y `MAIL_PASSWORD` con el App Password (16 caracteres sin espacios).
4. `MAIL_HOST=smtp.gmail.com`, `MAIL_PORT=587`, `MAIL_STARTTLS=true`.

## Schema Migrations (Flyway)

El schema está versionado con **Flyway**. Cada cambio = nuevo script en `src/main/resources/db/migration/` con naming `V{n}__descripcion.sql`.

- `V1__init_schema.sql` — estado inicial: `plantilla`, `notificacion` con índices (incluyendo el LONGTEXT de `contenido`).
- Cambios futuros: `V2__...sql`, `V3__...sql`. **NUNCA se edita un script ya aplicado** — siempre se agrega uno nuevo.
- Flyway corre **antes** que Hibernate: aplica los scripts pendientes y luego Hibernate valida (`ddl-auto: validate`) que las entidades calzan con el schema.
- Tabla de control: `flyway_schema_history` (la crea Flyway al arrancar).

### Variables relevantes

| Variable | Default | Descripción |
|----------|---------|-------------|
| `SPRING_FLYWAY_ENABLED` | `true` | Activa/desactiva Flyway |
| `SPRING_FLYWAY_BASELINE_ON_MIGRATE` | `false` | `true` solo si la DB ya tenía tablas pre-Flyway |
| `SPRING_FLYWAY_VALIDATE_ON_MIGRATE` | `true` | Valida checksums de scripts ya aplicados |

### Workflow primera vez

1. Crear el schema vacío en MySQL: `CREATE DATABASE notificacion_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. Levantar el servicio → Flyway aplica `V1` automáticamente.
3. Verificar con `SELECT version, description, success FROM flyway_schema_history;`.

## Ejecución Local (DEV)

```bash
# 1. Infra (MySQL + RabbitMQ + Kafka)
docker-compose -f docker-compose.infra.yml up -d

# 2. Variables
cp env.example .env
# editar .env (especialmente MAIL_*, AUTH_SERVICE_CLIENT_ID/SECRET, JWT_PUBLIC_KEY)

# 3. Levantar (auth-service debe estar arriba para que declare el exchange hotel.events)
mvn spring-boot:run

# Swagger UI
open http://localhost:8084/api/v1/swagger-ui.html
```

## Ejecución en Docker (PROD)

Multi-stage build, JRE 21 alpine, usuario no-root, healthcheck en `/api/v1/actuator/health`. Se levanta como parte de `docker-compose.prod.yml` (a definir) consumiendo `.env.prod` con todas las variables marcadas como obligatorias.

## Troubleshooting

| Síntoma | Causa probable | Solución |
|---------|----------------|----------|
| Emails no se envían | Credenciales SMTP inválidas o falta App Password | Regenerar App Password en Gmail; verificar `MAIL_USERNAME/PASSWORD` |
| Eventos de reserva no llegan | Kafka caído o `KAFKA_BOOTSTRAP_SERVERS` mal | `docker-compose -f docker-compose.infra.yml ps`; revisar topic name |
| Eventos de usuario no llegan | RabbitMQ caído o exchange `hotel.events` no creado | Verificar que `auth-service` esté arriba (declara el exchange) |
| Type mismatch al deserializar eventos RabbitMQ | Mapping de `RabbitConfig` desincronizado con clase del publisher | Revisar `RabbitConfig.java:97-112` |
| 401 en endpoints protegidos | `JWT_PUBLIC_KEY` no es pareja de la privada de auth | Regenerar keypair |
| `Could not resolve placeholder ...` | Falta env var obligatoria | Revisar la tabla |
