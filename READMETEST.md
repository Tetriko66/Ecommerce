# Guía para ejecutar los tests unitarios (EV3)

Este archivo es temporal para que puedas practicar antes de la defensa. Puedes borrarlo cuando ya no lo necesites.

## Qué son estos tests

Cada microservicio tiene **5 tests unitarios** sobre su **Service** (lógica de negocio). No levantan base de datos ni Spring completo: usan **JUnit 5 + Mockito** para simular el repositorio y las dependencias.

Patrón usado en todos:

- `@ExtendWith(MockitoExtension.class)` — activa Mockito
- `@Mock` — simula el repositorio (y WebClient cuando aplica)
- `@InjectMocks` — crea el service con los mocks inyectados
- `@BeforeEach setUp()` — prepara datos de prueba
- `assertThat(...)` — verifica el resultado (AssertJ)
- `assertThatThrownBy(...)` — verifica que se lance una excepción

## Requisitos

- **Java 17 o 21** (según el microservicio)
- **Maven** instalado y en el PATH
- Conexión a internet la primera vez (Maven descarga dependencias)

Verifica Maven:

```powershell
mvn -version
```

## Opción 1: Ejecutar desde la terminal (recomendado)

Abre PowerShell, entra a la carpeta del microservicio y ejecuta el test del service.

### Usuario

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\Usuario"
mvn test -Dtest=ServiceTest
```

### Carro

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\Carro"
mvn test -Dtest=ServiceTest
```

### Pedido

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\Pedido"
mvn test -Dtest=PedidoServiceTest
```

### Pago

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\Pago"
mvn test -Dtest=PagoServiceTest
```

### Producto

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\producto"
mvn test -Dtest=ProductoServiceTest
```

### Inventario

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\microServicioInventario"
mvn test -Dtest=InventarioServiceTest
```

### Despacho

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\Despacho"
mvn test -Dtest=DespachoServiceTest
```

### Reseñas (review)

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\review"
mvn test -Dtest=ResenaServiceTest
```

### Auth (login)

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\microServicioAuth"
mvn test -Dtest=AuthServiceTest
```

### Notificaciones

```powershell
cd "c:\Users\dell\Desktop\Escritorio\DuocUC\1FULLSTACK\EV3\Ecommerce\Ecommerce\microServicioNotificaciones"
mvn test -Dtest=NotificacionServiceTest
```

### Ejecutar un solo test dentro de la clase

Si quieres probar solo un método (por ejemplo el de login exitoso):

```powershell
mvn test -Dtest=AuthServiceTest#loginExitosoDevuelveToken
```

### Ejecutar todos los tests del microservicio (incluye ApplicationTests si existen)

```powershell
mvn test
```

**Importante:** si usas `mvn test` sin filtro, Maven también corre clases como `LoginApplicationTests` o `PagoApplicationTests`. Esas levantan Spring completo e intentan conectar a **MySQL**. Si la base de datos no está corriendo o la contraseña no coincide, verás `BUILD FAILURE` aunque tus 5 tests del Service hayan pasado.

Para la evaluación, usa el comando con `-Dtest=NombreDelServiceTest` (ver arriba). Así solo corren los tests unitarios del service, sin base de datos.

Ejemplo de salida cuando falla por MySQL (Auth):

```
Tests run: 5, Failures: 0  ← AuthServiceTest OK
Errors: 1                  ← LoginApplicationTests falló (MySQL)
Tests run: 6
BUILD FAILURE
```

Comando correcto solo para los 5 tests del service:

```powershell
mvn test -Dtest=AuthServiceTest
```

Debe decir `Tests run: 5` y `BUILD SUCCESS`.

## Opción 2: Ejecutar desde VS Code / Cursor

1. Abre el archivo del test, por ejemplo `ServiceTest.java`.
2. Verás iconos ▶ junto a cada `@Test` o a la clase.
3. Clic en **Run Test** sobre la clase (corre los 5) o sobre un método (corre solo ese).
4. Los resultados aparecen en el panel **Testing** o **Test Results**.

Si no aparecen los iconos, instala la extensión **Extension Pack for Java**.

## Cómo leer el resultado

**Éxito:**

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Fallo:** Maven muestra qué test falló y la línea del `assertThat` que no se cumplió.

## Ubicación de los archivos de test

| Microservicio   | Archivo de test |
|-----------------|-----------------|
| Usuario         | `Usuario/src/test/java/.../ServiceTest/ServiceTest.java` |
| Carro           | `Carro/src/test/java/.../TestService/ServiceTest.java` |
| Pedido          | `Pedido/src/test/java/.../ServiceTest/PedidoServiceTest.java` |
| Pago            | `Pago/src/test/java/.../TestService/PagoServiceTest.java` |
| Producto        | `producto/src/test/java/.../ServiceTest/ProductoServiceTest.java` |
| Inventario      | `microServicioInventario/src/test/java/.../ServiceTest/InventarioServiceTest.java` |
| Despacho        | `Despacho/src/test/java/.../ServiceTest/DespachoServiceTest.java` |
| Reseñas         | `review/src/test/java/.../ServiceTest/ResenaServiceTest.java` |
| Auth            | `microServicioAuth/src/test/java/.../ServiceTest/AuthServiceTest.java` |
| Notificaciones  | `microServicioNotificaciones/src/test/java/.../ServiceTest/NotificacionServiceTest.java` |

## Qué explicar en la defensa (ejemplo rápido)

Para cada test puedes decir:

1. **Given** — qué datos preparaste en `setUp()` y qué devuelve el mock (`when(...)`).
2. **When** — qué método del service llamaste.
3. **Then** — qué verificaste con `assertThat` o `assertThatThrownBy`.

Ejemplo (Usuario — email duplicado):

- **Given:** el repositorio dice que el email ya existe.
- **When:** llamo a `crearUsuario(request)`.
- **Then:** debe lanzar `IllegalArgumentException` con el mensaje de correo duplicado.

## Notas importantes

- **No necesitas MySQL** para estos tests: el repositorio está mockeado.
- Los tests de **Carro, Pedido, Inventario, Despacho y Reseñas** no prueban llamadas reales a otros microservicios (WebClient); solo la lógica que usa el repositorio local.
- Antes de la defensa, ejecuta al menos `mvn test -Dtest=NombreDelTest` en cada microservicio y confirma **BUILD SUCCESS**.

---

## Qué hace cada test unitario (por microservicio)

Resumen simple de los 5 tests de cada service. Sirve para estudiar o explicar en la defensa.

### Usuario (`ServiceTest`)

| Test | Qué prueba |
|------|------------|
| `crearUsuarioyDevuelveUsuarioCreado` | Si el email y username no existen, crea el usuario y devuelve id y username correctos. |
| `eliminarUsuarioyDevuelveUsuarioEliminado` | Si el usuario existe, lo elimina del repositorio. |
| `actualizarUsuarioyDevuelveActualizado` | Si el usuario existe, actualiza sus datos y devuelve el username nuevo. |
| `CrearUsuarioconUsernameDuplicado` | Si el username ya existe, lanza error y no crea el usuario. |
| `CrearUsuarioconEmailDuplicado` | Si el email ya existe, lanza error y no crea el usuario. |

### Carro (`ServiceTest`)

| Test | Qué prueba |
|------|------------|
| `listarDevuelveLista` | Al listar el carrito, devuelve los items con producto y cantidad correctos. |
| `listarDevuelveListaVacia` | Si no hay items en el carrito, devuelve una lista vacía. |
| `eliminarExitosoEliminaDelRepositorio` | Si el item existe, lo elimina del repositorio. |
| `eliminarCarritoQueNoExisteLanzaExcepcion` | Si el item no existe, lanza error "Carrito no encontrado". |
| `listarConMultiplesElementosDevuelveTodos` | Si hay varios items, los devuelve todos en la lista. |

### Pedido (`PedidoServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodosDevuelveLista` | Devuelve todos los pedidos; verifica que el estado sea PAGADO. |
| `obtenerPorIdDevuelvePedido` | Busca un pedido por id y devuelve id y monto correctos. |
| `obtenerPorIdNoEncontradoLanzaExcepcion` | Si el pedido no existe, lanza error 404. |
| `eliminarExitoso` | Si el pedido existe, lo elimina del repositorio. |
| `eliminarPedidoNoEncontradoLanzaExcepcion` | Si el pedido no existe al eliminar, lanza error 404. |

### Pago (`PagoServiceTest`)

| Test | Qué prueba |
|------|------------|
| `registrarPagoDevuelvePagoCreado` | Registra un pago y devuelve monto y usuario correctos. |
| `listarPagosDevuelveLista` | Lista todos los pagos registrados. |
| `actualizarMontoDevuelvePagoActualizado` | Si el pago existe, actualiza el monto y lo devuelve. |
| `actualizarMontoPagoNoEncontradoLanzaExcepcion` | Si el pago no existe, lanza error "Pago no encontrado". |
| `eliminarPagoEliminaDelRepositorio` | Elimina el pago del repositorio por id. |

### Producto (`ProductoServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodosDevuelveLista` | Devuelve la lista de productos con el nombre correcto. |
| `obtenerPorIdDevuelveProducto` | Busca un producto por id y devuelve id y precio. |
| `obtenerPorIdNoEncontradoLanzaExcepcion` | Si el producto no existe, lanza error 404. |
| `desactivarMarcaProductoComoInactivo` | Desactiva un producto (baja lógica) y lo guarda como inactivo. |
| `eliminarProductoNoEncontradoLanzaExcepcion` | Si el producto no existe al eliminar, lanza error 404. |

### Inventario (`InventarioServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodosDevuelveLista` | Devuelve todos los registros de inventario. |
| `obtenerPorIdDevuelveInventario` | Busca inventario por id y devuelve la cantidad correcta. |
| `obtenerPorIdNoEncontradoLanzaExcepcion` | Si no existe inventario con ese id, lanza error 404. |
| `agregarStockSumaCantidad` | Suma unidades al stock (ej. 20 + 5 = 25). |
| `reducirStockInsuficienteLanzaExcepcion` | Si se piden más unidades de las disponibles, lanza error de stock insuficiente. |

### Despacho (`DespachoServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodosDevuelveLista` | Devuelve todos los despachos registrados. |
| `obtenerPorIdDevuelveDespacho` | Busca un despacho por id y devuelve su estado. |
| `obtenerPorIdNoEncontradoLanzaExcepcion` | Si el despacho no existe, lanza error 404. |
| `actualizarEstadoDevuelveDespachoActualizado` | Cambia el estado del despacho (ej. a EN_CAMINO). |
| `eliminarDespachoNoEncontradoLanzaExcepcion` | Si el despacho no existe al eliminar, lanza error 404. |

### Reseñas (`ResenaServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodosDevuelveLista` | Devuelve todas las reseñas con su calificación. |
| `obtenerPorIdDevuelveResena` | Busca una reseña por id y devuelve el comentario. |
| `obtenerPorIdNoEncontradaLanzaExcepcion` | Si la reseña no existe, lanza error 404. |
| `obtenerPromedioProductoDevuelvePromedio` | Calcula el promedio de calificaciones de un producto. |
| `obtenerPromedioProductoSinResenasDevuelveCero` | Si no hay reseñas, el promedio es 0.0. |

### Auth (`AuthServiceTest`)

| Test | Qué prueba |
|------|------------|
| `loginExitosoDevuelveToken` | Con usuario y password correctos, devuelve un token JWT. |
| `loginUsuarioNoExisteLanzaExcepcion` | Si el username no existe, lanza error de credenciales incorrectas. |
| `loginPasswordIncorrectoLanzaExcepcion` | Si la contraseña no coincide, lanza error de credenciales incorrectas. |
| `loginUsuarioDeshabilitadoLanzaExcepcion` | Si el usuario está deshabilitado, lanza error específico. |
| `loginPasswordCorrectoGeneraTokenConRol` | Verifica que el JwtService genere el token con username y rol. |

### Notificaciones (`NotificacionServiceTest`)

| Test | Qué prueba |
|------|------------|
| `obtenerTodasDevuelveLista` | Devuelve todas las notificaciones con destinatario correcto. |
| `obtenerPorIdDevuelveNotificacion` | Busca una notificación por id y devuelve su estado. |
| `obtenerPorIdNoEncontradaLanzaExcepcion` | Si la notificación no existe, lanza error 404. |
| `marcarComoEnviadaCambiaEstado` | Marca la notificación como ENVIADO y registra fecha de envío. |
| `crearNotificacionQuedaEnEstadoPendiente` | Al crear una notificación, queda en estado PENDIENTE. |
