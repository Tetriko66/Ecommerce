CREATE TABLE IF NOT EXISTS despacho (
    id_despacho       BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id         BIGINT NOT NULL,
    direccion_entrega VARCHAR(255) NOT NULL,
    estado            VARCHAR(20) NOT NULL DEFAULT 'PREPARANDO',
    fecha_despacho    DATETIME NOT NULL,
    fecha_entrega     DATETIME
);
