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
