CREATE TABLE IF NOT EXISTS pagos (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT NOT NULL,
    pedido_id   BIGINT NOT NULL,
    monto       DOUBLE NOT NULL,
    metodo_pago VARCHAR(50),
    fecha_pago  DATETIME DEFAULT CURRENT_TIMESTAMP
);
