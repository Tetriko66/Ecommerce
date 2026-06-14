# Hotel Microservicios - Proyecto de Grupo

Sistema de ecommerce basado en arquitectura de microservicios con Spring Boot y Java 21.

## Integrantes
- Vicente Rodriguez
- Alessandro Nava
- Bruno Decinti

---

## Requisitos previos

- Java 21
- Maven 3.9+
- MySQL corriendo en `localhost:3307`
- Postman (para pruebas de endpoints)

---

## Bases de datos requeridas

Deben existir en MySQL **antes** de levantar los servicios. Flyway crea las tablas automáticamente al arrancar cada microservicio.

```sql
CREATE DATABASE db_usuario;
CREATE DATABASE db_producto;
CREATE DATABASE db_inventario;
CREATE DATABASE db_notificacion;
CREATE DATABASE db_carrito;
CREATE DATABASE db_perfil;
CREATE DATABASE db_resena;
CREATE DATABASE db_pago;
CREATE DATABASE db_pedido;
CREATE DATABASE db_envio;
```

---

## Orden de arranque

Respetar este orden es importante por las dependencias entre servicios:

| # | Microservicio | Puerto | Base de datos |
|---|---|---|---|
| 1 | microServicioAuth | 8083 | db_usuario |
| 2 | Producto | 8082 | db_producto |
| 3 | Usuario | 8087 | db_perfil |
| 4 | microServicioInventario | 8084 | db_inventario |
| 5 | microServicioNotificaciones | 8085 | db_notificacion |
| 6 | Carro | 8086 | db_carrito |
| 7 | Review | 8088 | db_resena |
| 8 | Pago | 8089 | db_pago |
| 9 | Pedido | 8090 | db_pedido |
| 10 | Despacho | 8091 | db_envio |

### Comando para levantar cada microservicio

Desde la carpeta de cada microservicio:

```bash
mvn spring-boot:run
```

---

## Autenticación JWT

Todos los endpoints de **mutación** (POST, PUT, PATCH, DELETE) requieren un token JWT válido.
Los endpoints de **consulta** (GET) son públicos.

### Paso 1 — Obtener el token

```
POST http://localhost:8083/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "1234"
}
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Paso 2 — Usar el token en Postman

En cada request que requiera autenticación:
- Pestaña **Authorization** → Type: `Bearer Token`
- Pegar el token recibido

O manualmente en el header:
```
Authorization: Bearer <token>
```

> **Duración del token:** 1 hora (3600000 ms)

---

## Dependencias entre microservicios

```
microServicioAuth  ──────────────────────────► db_usuario
Producto           ──────────────────────────► db_producto
Usuario            ──────────────────────────► db_perfil
microServicioInventario  ──► Producto (8082) ► db_inventario
microServicioNotificaciones ─────────────────► db_notificacion
Carro    ──► Producto (8082) + Inventario (8084) ► db_carrito
Review   ──► Usuario (8087) + Producto (8082)    ► db_resena
Pago     ────────────────────────────────────── ► db_pago
Pedido   ──► Inventario (8084) + Pago (8089)    ► db_pedido
Despacho ──► Pedido (8090)                      ► db_envio
```

---

## Endpoints principales

### Auth (8083)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | /api/v1/auth/login | Login y obtención de token | No |

### Producto (8082)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /api/v1/productos | Listar productos | No |
| GET | /api/v1/productos/{id} | Obtener producto | No |
| POST | /api/v1/productos | Crear producto | Sí |
| PUT | /api/v1/productos/{id} | Actualizar producto | Sí |
| DELETE | /api/v1/productos/{id} | Eliminar producto | Sí |

### Inventario (8084)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /api/v1/inventario | Listar inventario | No |
| GET | /api/v1/inventario/producto/{id} | Stock de un producto | No |
| POST | /api/v1/inventario | Crear registro | Sí |
| PATCH | /api/v1/inventario/producto/{id}/agregar | Agregar stock | Sí |
| PATCH | /api/v1/inventario/producto/{id}/reducir | Reducir stock | Sí |

### Carro (8086)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /carrito | Listar carrito | No |
| POST | /carrito | Agregar al carrito | Sí |
| PATCH | /carrito/{id}/cantidad | Actualizar cantidad | Sí |
| DELETE | /carrito/{id} | Eliminar del carrito | Sí |

### Pedido (8090)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /api/v1/pedidos | Listar pedidos | No |
| GET | /api/v1/pedidos/{id} | Obtener pedido | No |
| GET | /api/v1/pedidos/usuario/{id} | Pedidos de un usuario | No |
| POST | /api/v1/pedidos | Crear pedido | Sí |
| PATCH | /api/v1/pedidos/{id}/estado | Cambiar estado | Sí |

### Despacho (8091)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /api/v1/despachos | Listar despachos | No |
| GET | /api/v1/despachos/pedido/{id} | Despacho de un pedido | No |
| POST | /api/v1/despachos | Crear despacho | Sí |
| PATCH | /api/v1/despachos/{id}/estado | Cambiar estado | Sí |

### Review (8088)
| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| GET | /api/v1/resenas | Listar reseñas | No |
| GET | /api/v1/resenas/producto/{id} | Reseñas de un producto | No |
| POST | /api/v1/resenas | Crear reseña | Sí |
| DELETE | /api/v1/resenas/{id} | Eliminar reseña | Sí |

---

## Flujo completo de prueba

1. **Login** → `POST :8083/api/v1/auth/login` → obtener token
2. **Crear producto** → `POST :8082/api/v1/productos`
3. **Crear inventario** → `POST :8084/api/v1/inventario` (asociar producto con stock)
4. **Agregar al carrito** → `POST :8086/carrito`
5. **Crear pedido** → `POST :8090/api/v1/pedidos` (descuenta stock y registra pago)
6. **Crear despacho** → `POST :8091/api/v1/despachos`
7. **Marcar como entregado** → `PATCH :8091/api/v1/despachos/{id}/estado?estado=ENTREGADO`

---

## Usuarios de prueba (cargados por Flyway)

| Usuario | Password |
|---|---|
| vicente | 123456 |
| maria | abcdef |
