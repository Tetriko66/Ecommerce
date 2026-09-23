INSERT INTO notificacion (tipo, destinatario, asunto, mensaje, estado, fecha_creacion) VALUES
('EMAIL', 'cliente1@example.com', 'Confirmación de reserva', 'Su reserva ha sido confirmada exitosamente.', 'ENVIADO', NOW()),
('EMAIL', 'cliente2@example.com', 'Recordatorio de check-in', 'Le recordamos que su check-in es mañana a las 14:00.', 'ENVIADO', NOW()),
('SMS',   '+56912345678',         'Reserva confirmada',      'Hotel DuocUC: Su reserva fue confirmada. Gracias.', 'ENVIADO', NOW()),
('EMAIL', 'cliente3@example.com', 'Bienvenido al hotel',     'Estimado cliente, bienvenido. Su habitación está lista.', 'PENDIENTE', NOW()),
('EMAIL', 'cliente4@example.com', 'Factura disponible',      'Su factura de estadía ya está disponible para descargar.', 'FALLIDO', NOW());
