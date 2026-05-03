-- =========================================================
-- ms-notificacion-service: Baseline schema
-- =========================================================
-- Refleja el estado actual de las entidades JPA:
--   Plantilla, Notificacion
-- A partir de aquí, cada cambio de schema = nuevo script V{n}__descripcion.sql
-- =========================================================

CREATE TABLE plantilla (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    codigo              VARCHAR(255),
    nombre              VARCHAR(255),
    asunto              VARCHAR(255),
    contenido           VARCHAR(255),
    tipo                VARCHAR(255),
    activa              BIT(1),
    fecha_creacion      DATETIME(6),
    fecha_modificacion  DATETIME(6),
    CONSTRAINT pk_plantilla PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_plantilla_codigo ON plantilla (codigo);

CREATE TABLE notificacion (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    tipo            VARCHAR(255),
    event_type      VARCHAR(255),
    destinatario    VARCHAR(255),
    asunto          VARCHAR(255),
    contenido       LONGTEXT,
    mensaje         VARCHAR(255),
    estado          VARCHAR(255),
    fecha_creacion  DATETIME(6),
    fecha_envio     DATETIME(6),
    intentos        INT,
    error_mensaje   VARCHAR(255),
    user_id         BIGINT,
    leida           BIT(1),
    CONSTRAINT pk_notificacion PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_notificacion_user_id        ON notificacion (user_id);
CREATE INDEX idx_notificacion_estado         ON notificacion (estado);
CREATE INDEX idx_notificacion_event_type     ON notificacion (event_type);
CREATE INDEX idx_notificacion_destinatario   ON notificacion (destinatario);
CREATE INDEX idx_notificacion_fecha_creacion ON notificacion (fecha_creacion);
