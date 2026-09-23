CREATE TABLE IF NOT EXISTS resena (
    id_resena BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    calificacion INT NOT NULL,
    comentario TEXT,
    fecha_resena DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_calificacion CHECK (calificacion BETWEEN 1 AND 5)
);
