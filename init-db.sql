-- Script de inicialización: crea todas las bases de datos del proyecto.
-- Docker lo ejecuta automáticamente al levantar el contenedor MySQL por primera vez.

CREATE DATABASE IF NOT EXISTS db_usuario;
CREATE DATABASE IF NOT EXISTS db_producto;
CREATE DATABASE IF NOT EXISTS db_inventario;
CREATE DATABASE IF NOT EXISTS db_notificacion;
CREATE DATABASE IF NOT EXISTS db_carrito;
CREATE DATABASE IF NOT EXISTS db_perfil;
CREATE DATABASE IF NOT EXISTS db_resena;
CREATE DATABASE IF NOT EXISTS db_pago;
CREATE DATABASE IF NOT EXISTS db_pedido;
CREATE DATABASE IF NOT EXISTS db_envio;
