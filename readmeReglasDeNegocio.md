# Reglas de negocio — Ecommerce Microservicios

Referencia de validaciones (**DTO**) y reglas de lógica (**Service**) por microservicio.  
Útil para la defensa, pruebas en Swagger y entender qué puede fallar y por qué.

---

## Reglas globales del proyecto

| Regla | Detalle |
|-------|---------|
| **Autenticación JWT** | Los endpoints **GET** son públicos. **POST, PUT, PATCH y DELETE** requieren token JWT (excepto login). |
| **Dos stocks distintos** | El campo `stock` de **Producto** es del catálogo. El stock **real** para compras está en **Inventario** (`cantidad`). Pedido y Carro usan Inventario, no Producto. |
| **Dos tipos de usuario** | **Auth** (`db_usuario`) valida login. **Usuario** (`db_perfil`) es el perfil del ecommerce. Son bases de datos distintas. |
| **Validación en capas** | Primero valida el **DTO** (`@Valid` → 400). Luego el **Service** (reglas de negocio → 404, 409, 503, etc.). |
| **Comunicación entre servicios** | Vía **WebClient** (HTTP). Si el servicio destino no responde → **503 SERVICE_UNAVAILABLE**. |

---

## 1. Auth (`AuthService`) — puerto 8083

### Validaciones del DTO (`DtoAuthRequest`)

| Campo | Regla |
|-------|--------|
| `username` | Obligatorio (`@NotBlank`) |
| `password` | Obligatorio (`@NotBlank`) |

### Reglas del Service — `login`

| # | Regla | Error |
|---|--------|-------|
| 1 | El `username` debe existir en `db_usuario` | **401** — "Usuario o password incorrecto" |
| 2 | El usuario debe tener `enabled = true` | **401** — "Usuario deshabilitado" |
| 3 | La contraseña debe coincidir **exactamente** con la guardada (texto plano, sin hash) | **401** — "Usuario o password incorrecto" |
| 4 | Si todo es válido, se genera JWT con `username`, rol y expiración configurada (`jwt.expiration`, 1 hora) | **200** + token |

> No hay registro de usuarios en Auth desde la API; los usuarios se insertan por Flyway o directamente en BD.

---

## 2. Producto (`ProductoService`) — puerto 8082

### Validaciones del DTO (`DtoProductoRequest`)

| Campo | Regla |
|-------|--------|
| `nombre` | Obligatorio (`@NotBlank`) |
| `descripcion` | Opcional |
| `precio` | Obligatorio, mínimo **0.01** (`@DecimalMin("0.01")`) |
| `categoria` | Obligatorio (`@NotBlank`) |
| `stock` | Obligatorio, mínimo **0** (`@Min(0)`) — *solo catálogo, no afecta ventas* |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Obtener por id** | El producto debe existir | **404** — "Producto no encontrado con id: X" |
| **Crear** | Siempre se guarda con `activo = true` y `fechaCreacion = now` | — |
| **Actualizar** | El producto debe existir | **404** |
| **Desactivar** | Baja lógica: `activo = false` (no borra el registro) | **404** si no existe |
| **Eliminar** | Borrado físico de la BD | **404** si no existe |
| **Listar activos** | Solo productos con `activo = true` | — |

> Crear un producto **no crea** inventario automáticamente.

---

## 3. Inventario (`InventarioService`) — puerto 8084

### Validaciones del DTO

**`DtoInventarioRequest` (crear inventario)**

| Campo | Regla |
|-------|--------|
| `productoId` | Obligatorio (`@NotNull`) |
| `cantidad` | Obligatorio, mínimo **0** (`@Min(0)`) — *permite stock 0* |
| `stockMinimo` | Obligatorio, mínimo **0** (`@Min(0)`) |

**`DtoAjusteStockRequest` (agregar / reducir stock)**

| Campo | Regla |
|-------|--------|
| `cantidad` | Obligatorio, mínimo **1** (`@Min(1)`) |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear** | El producto debe existir en Producto (llamada HTTP) | **404** — producto no existe |
| **Crear** | El producto debe existir en Producto (servicio caído) | **503** — no se pudo conectar con productos |
| **Crear** | Solo **un** registro de inventario por `productoId` | **409** — "Ya existe un inventario para el producto..." |
| **Obtener por id / producto** | Debe existir el registro | **404** |
| **Agregar stock** | Debe existir inventario para ese producto | **404** |
| **Agregar stock** | Suma la cantidad al stock actual | — |
| **Reducir stock** | Debe existir inventario para ese producto | **404** |
| **Reducir stock** | `cantidad` solicitada ≤ stock disponible | **409** — "Stock insuficiente. Disponible: X, Solicitado: Y" |
| **Reducir stock** | Si tras reducir queda `cantidad < stockMinimo`, solo **loguea alerta** (no bloquea) | — |
| **Eliminar** | Debe existir el registro | **404** |
| **Respuesta** | Incluye flag `stockBajo = true` si `cantidad < stockMinimo` | — |

---

## 4. Carro (`ServiceCarro`) — puerto 8086

### Validaciones del DTO (`Request`)

| Campo | Regla |
|-------|--------|
| `usuarioId` | Obligatorio (`@NotNull`) — *sin mínimo/máximo adicional* |
| `productoId` | Obligatorio (`@NotNull`) |
| `cantidad` | Obligatorio (`@NotNull`) — **no tiene `@Min(1)`** en el DTO |

> **Atención:** el DTO permite `cantidad = 0` o negativa. El Service no valida explícitamente un mínimo; la regla práctica es que debe haber stock suficiente en Inventario.

**PATCH `/carrito/{id}/cantidad`:** el parámetro `cantidad` viene por query string **sin validación `@Valid`**.

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Agregar** | El producto debe existir (consulta a Producto) | **404** — producto no existe |
| **Agregar** | Debe existir inventario para el producto | **404** — no hay inventario |
| **Agregar / actualizar cantidad** | Stock en Inventario ≥ cantidad solicitada | **409** — stock insuficiente |
| **Agregar / actualizar cantidad** | Producto o Inventario no responde | **503** |
| **Actualizar cantidad** | El ítem del carrito debe existir | **404** — `EntityNotFoundException` |
| **Eliminar** | El ítem debe existir | **404** |
| **Listar** | Devuelve **todos** los ítems de **todos** los usuarios (no filtra por usuario) | — |

> El carrito **no descuenta stock**; solo **consulta** Inventario al agregar o cambiar cantidad.

---

## 5. Usuario / Perfil (`UsuarioService`) — puerto 8087

### Validaciones del DTO (`Request`)

| Campo | Regla |
|-------|--------|
| `username` | Obligatorio, entre **3 y 20** caracteres (`@Size(min=3, max=20)`) |
| `password` | Obligatorio, mínimo **6** caracteres (`@Size(min=6)`) |
| `email` | Obligatorio, formato email válido (`@Email`) |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear** | El `email` no puede estar duplicado | **400** — `IllegalArgumentException`: "El correo ya existe..." |
| **Crear** | El `username` no puede estar duplicado | **400** — "El nombre de usuario ya existe..." |
| **Obtener por id** | Debe existir | **400** — "Usuario no encontrado con ID: X" |
| **Actualizar** | Debe existir | **400** si no existe |
| **Actualizar** | **No valida** duplicados de email/username al actualizar | — |
| **Eliminar** | Debe existir | **400** si no existe |

> Este servicio es el que valida **Reseñas** al crear (`GET /api/usuarios/{id}`).

---

## 6. Reseñas (`ResenaService`) — puerto 8088

### Validaciones del DTO (`DtoResenaRequest`)

| Campo | Regla |
|-------|--------|
| `usuarioId` | Obligatorio (`@NotNull`) |
| `productoId` | Obligatorio (`@NotNull`) |
| `calificacion` | Obligatorio, entre **1 y 5** (`@Min(1)`, `@Max(5)`) |
| `comentario` | Opcional (puede ser null o vacío) |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear** | El usuario debe existir en Usuario (perfil) | **404** / **503** |
| **Crear** | El producto debe existir en Producto | **404** / **503** |
| **Crear** | Un usuario solo puede reseñar **una vez** el mismo producto | **409** — "El usuario ya tiene una reseña para este producto" |
| **Crear** | Asigna `fechaResena = now` automáticamente | — |
| **Obtener por id** | Debe existir | **404** |
| **Actualizar** | Debe existir; solo cambia calificación y comentario (no usuario/producto) | **404** |
| **Eliminar** | Debe existir | **404** |
| **Promedio por producto** | Si no hay reseñas, devuelve **0.0** | — |

---

## 7. Pago (`PagoService`) — puerto 8089

### Validaciones del DTO (`Request`)

| Campo | Regla |
|-------|--------|
| `usuarioId` | Obligatorio (`@NotNull`) |
| `pedidoId` | Obligatorio (`@NotNull`) |
| `monto` | Obligatorio (`@NotNull`) — *sin mínimo ni máximo en DTO* |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Registrar pago** | Guarda usuario, pedido y monto; **no valida** que el pedido exista ni que el monto sea positivo | — |
| **Listar** | Devuelve todos los pagos | — |
| **Actualizar monto** | El pago debe existir | **500** — `RuntimeException`: "Pago no encontrado" |
| **Eliminar** | Borra por id **sin comprobar** si existe | — |

> Cuando **Pedido** crea un pedido, llama a Pago con `pedidoId: 0` (el pedido aún no tiene id guardado).

---

## 8. Pedido (`PedidoService`) — puerto 8090

### Validaciones del DTO (`DtoPedidoRequest`)

| Campo | Regla |
|-------|--------|
| `usuarioId` | Obligatorio (`@NotNull`) |
| `productoId` | Obligatorio (`@NotNull`) |
| `cantidad` | Obligatorio, mínimo **1** (`@Min(1)`) |
| `montoTotal` | Obligatorio (`@NotNull`) — *sin validación de valor mínimo* |
| `metodoPago` | Obligatorio, no vacío (`@NotBlank`) — *cualquier texto, ej. "TARJETA"* |

### Reglas del Service — crear pedido (orden estricto)

```
1. Reducir stock en Inventario
2. Registrar pago en Pago
3. Guardar pedido en BD con estado PAGADO
```

Si falla el paso 1 o 2, **no se crea el pedido**.

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear — paso 1** | Debe existir inventario para el producto | **404** |
| **Crear — paso 1** | Stock ≥ cantidad pedida | **409** — stock insuficiente |
| **Crear — paso 1** | Inventario no responde | **503** |
| **Crear — paso 2** | Registra pago en Pago; si falla la conexión | **503** |
| **Crear — paso 3** | Estado inicial siempre **`PAGADO`**; `fechaPedido = now` | — |
| **Obtener por id** | Debe existir | **404** |
| **Obtener por estado** | Convierte el estado a **MAYÚSCULAS** antes de buscar | — |
| **Actualizar estado** | Debe existir; guarda estado en mayúsculas | **404** |
| **Eliminar** | Debe existir | **404** |

---

## 9. Despacho (`DespachoService`) — puerto 8091

### Validaciones del DTO (`DtoDespachoRequest`)

| Campo | Regla |
|-------|--------|
| `pedidoId` | Obligatorio (`@NotNull`) |
| `direccionEntrega` | Obligatorio, no vacío (`@NotBlank`) |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear** | El pedido debe existir (consulta a Pedido) | **404** / **503** |
| **Crear** | Solo **un despacho por pedido** | **409** — "Ya existe un despacho para el pedido..." |
| **Crear** | Intenta cambiar el pedido a estado **`DESPACHADO`** (si falla, **solo loguea**, no cancela el despacho) | — |
| **Crear** | Estado inicial del despacho: **`PREPARANDO`**; `fechaEntrega = null` | — |
| **Actualizar estado** | Debe existir; normaliza estado a mayúsculas | **404** |
| **Actualizar estado** | Si estado = **`ENTREGADO`**, asigna `fechaEntrega = now` | — |
| **Obtener por pedido** | Debe existir despacho para ese pedido | **404** |
| **Obtener por estado** | Busca con estado en mayúsculas | — |
| **Eliminar** | Debe existir | **404** |

---

## 10. Notificaciones (`NotificacionService`) — puerto 8085

### Validaciones del DTO (`DtoNotificacionRequest`)

| Campo | Regla |
|-------|--------|
| `tipo` | Obligatorio (`@NotNull`) — enum: `EMAIL`, `SMS`, `PUSH` |
| `destinatario` | Obligatorio (`@NotBlank`) |
| `asunto` | Obligatorio (`@NotBlank`) |
| `mensaje` | Obligatorio (`@NotBlank`) |

### Reglas del Service

| Operación | Regla | Error |
|-----------|--------|-------|
| **Crear** | Estado inicial siempre **`PENDIENTE`**; `fechaEnvio = null`; `fechaCreacion = now` | — |
| **Marcar enviada** | Debe existir; pasa a **`ENVIADO`** y `fechaEnvio = now` | **404** |
| **Marcar fallida** | Debe existir; pasa a **`FALLIDO`** | **404** |
| **Obtener por id** | Debe existir | **404** |
| **Eliminar** | Debe existir | **404** |

**Estados posibles:** `PENDIENTE` → `ENVIADO` o `FALLIDO`

> Notificaciones **no se llama** automáticamente desde Pedido o Despacho en el código actual; es un servicio independiente.

---

## Flujo de compra — reglas encadenadas

Para que un pedido funcione en Swagger:

| Paso | Servicio | Regla clave |
|------|----------|-------------|
| 1 | Auth | Login válido → JWT |
| 2 | Producto | Producto debe existir |
| 3 | Inventario | Debe existir registro para ese `productoId` con `cantidad ≥ 1` |
| 4 | Pedido | `cantidad ≥ 1`; reduce Inventario; registra Pago; guarda con estado `PAGADO` |
| 5 | Despacho (opcional) | Pedido existe; no hay otro despacho para ese pedido |

Para agregar al carrito:

| Paso | Regla |
|------|--------|
| 1 | Producto existe |
| 2 | Inventario existe y `cantidad ≥ cantidad del carrito` |

---

## Resumen de códigos HTTP por tipo de regla

| Código | Cuándo aparece |
|--------|----------------|
| **400** | Validación DTO fallida (`@Valid`, `@Min`, `@Max`, etc.) |
| **401** | Login incorrecto o usuario deshabilitado (Auth) |
| **404** | Recurso no encontrado (id inexistente, sin inventario, producto no existe) |
| **409** | Conflicto de negocio (stock insuficiente, duplicados, despacho/reseña ya existe) |
| **503** | Microservicio dependiente no responde (WebClient) |
| **500** | Error no controlado o excepciones genéricas (ej. Pago no encontrado) |

---

## Gaps / comportamientos a tener en cuenta (no son reglas explícitas)

Estos puntos **no están implementados** como regla, pero conviene conocerlos:

- **Carro:** `cantidad` sin `@Min(1)` en el DTO; PATCH de cantidad sin validación.
- **Pago:** no valida monto positivo ni existencia del pedido.
- **Usuario:** al actualizar no revisa email/username duplicados.
- **Producto.stock** vs **Inventario.cantidad** no se sincronizan.
- **Despacho:** si falla actualizar el estado del pedido a DESPACHADO, el despacho igual se crea.
- **Auth** y **Usuario (perfil)** son usuarios distintos en BDs distintas.

---

*Documento generado a partir de los archivos `*Service.java` y DTOs de request de cada microservicio.*
