CREATE TABLE IF NOT EXISTS pedido (
    id_pedido   BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT NOT NULL,
    monto_total DOUBLE NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado      VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pedido DATETIME NOT NULL
);
