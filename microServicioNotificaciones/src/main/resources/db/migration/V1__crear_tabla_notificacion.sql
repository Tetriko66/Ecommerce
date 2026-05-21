CREATE TABLE IF NOT EXISTS notificacion (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo            VARCHAR(10)  NOT NULL,
    destinatario    VARCHAR(255) NOT NULL,
    asunto          VARCHAR(255) NOT NULL,
    mensaje         TEXT         NOT NULL,
    estado          VARCHAR(10)  NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_envio     DATETIME
);
