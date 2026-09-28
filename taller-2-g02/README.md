# Gestion de Usuarios (HU01)

Proyecto Java + Maven que implementa la historia de usuario **HU01. Gestion
de usuarios del sistema**, aplicando los principios SOLID, y persistiendo
en **SQLite**.

## Requisitos cubiertos

| Requisito | Descripcion | Donde vive |
|---|---|---|
| RF-01 | Registrar usuarios que interactuan con la aplicacion | `UserService.registerUser(...)` |
| RF-02 | Autenticacion mediante usuario y contrasena | `AuthService.authenticate(...)` |
| RF-03 | Administrar diferentes roles (Administrador, Autor de preguntas, Revisor, Docente, Estudiante) | enum `Role`, `UserService.changeRole(...)` |

## Estructura del proyecto

```
src/main/java/co/unicauca/gestionusuarios/
├── domain/
│   ├── User.java            Entidad de dominio (solo datos)
│   ├── Role.java            Enum con los 5 roles del sistema
│   └── UserState.java       Enum ACTIVO/INACTIVO
├── repository/
│   ├── IUserRepository.java Abstraccion de persistencia (DIP)
│   └── impl/
│       ├── InMemoryUserRepository.java   Implementacion en memoria (pruebas)
│       └── SQLiteUserRepository.java     Implementacion real con SQLite
├── security/
│   ├── IPasswordHasher.java         Abstraccion de hashing (DIP)
│   └── Sha256PasswordHasher.java    Implementacion SHA-256 + sal
├── service/
│   ├── UserService.java     Registro y administracion (RF-01, RF-03)
│   └── AuthService.java     Autenticacion (RF-02)
├── exception/                Excepciones de negocio especificas
└── main/
    └── Main.java              Demo ejecutable
```

## Interfaz grafica (JavaFX)

Se agrego una capa de UI en `co.unicauca.gestionusuarios.ui`:

```
ui/
├── AppLauncher.java     Punto de entrada JavaFX (composition root)
├── LoginView.java       Pantalla de inicio de sesion (RF-02)
├── UserAdminView.java   CRUD de usuarios para el rol Administrador (RF-01, RF-03)
├── UserFormDialog.java  Formulario modal reutilizable (crear/editar)
└── BlankHomeView.java   Pantalla en blanco para los demas roles (por ahora)
```

**Flujo:**

1. `AppLauncher` arma los servicios (`UserService`, `AuthService`) igual que
   `Main.java`, y crea un usuario `admin` / `admin123` automaticamente la
   primera vez que la base de datos esta vacia.
2. `LoginView` valida usuario/contrasena contra `AuthService`.
3. Si el rol autenticado es `ADMINISTRADOR`, se abre `UserAdminView`
   (tabla con todos los usuarios + botones Agregar / Editar / Eliminar /
   Refrescar).
4. Para cualquier otro rol (Autor de preguntas, Revisor, Docente,
   Estudiante) se muestra `BlankHomeView`, una pantalla en blanco lista
   para implementarse en una iteracion futura.

La UI **no** conoce `SQLiteUserRepository` ni JDBC: solo habla con
`UserService`/`AuthService`, siguiendo el mismo DIP que ya usa la version
de consola.

### Ejecutar la version grafica

```bash
mvn javafx:run
```

Esto abre la ventana de login. Credenciales del administrador semilla:

```
Usuario:     admin
Contrasena:  admin123
```

> Nota: la primera vez, Maven descargara los binarios nativos de JavaFX
> correspondientes a tu sistema operativo (Windows/Mac/Linux), por eso
> necesitas conexion a internet en la primera ejecucion.

## Conectar una base de datos SQLite YA EXISTENTE

Si ya tienes un archivo `.db` con datos previos (por ejemplo generado con
un script como este):

```sql
create table usuario (
    usu_id integer primary key,
    usu_usuario varchar(50) not null unique,
    usu_nombre varchar(50) not null,
    usu_rol varchar(20) not null,
    usu_estado varchar(15) not null,
    password varchar(255) not null
);
```

el proyecto lo soporta de forma nativa, siempre que la tabla se llame
`usuario` y tenga esas columnas. Pasos:

1. Copia tu archivo `.db` a la raiz del proyecto (donde esta `pom.xml`)
   y renombralo a `data.db` (o cambia el nombre en `AppLauncher.java` /
   `Main.java` si prefieres mantener tu nombre original).
2. Ejecuta `mvn javafx:run` o `mvn exec:java` normalmente. El codigo usa
   `CREATE TABLE IF NOT EXISTS`, asi que si la tabla ya existe **no la
   toca ni borra tus datos**.

### Sobre roles y estado en texto libre

Si tus datos guardan el rol/estado con texto "humano" (por ejemplo
`'Administrador'`, `'Autor de preguntas'`, `'activo'`, `'inactivo'`, en
vez de `ADMINISTRADOR`/`ACTIVO` en mayusculas), no hay que tocar nada:
los enums `Role` y `UserState` ya mapean cada valor de negocio a su texto
exacto en base de datos mediante `getDbValue()` / `fromDbValue(...)`. Por
ejemplo:

```java
Role.ADMINISTRADOR.getDbValue();      // "Administrador"
Role.fromDbValue("Autor de preguntas"); // Role.AUTOR_PREGUNTAS
```

Si tu base usa un texto distinto a los que trae el proyecto por defecto,
solo hay que ajustar el segundo argumento del constructor de cada
constante en `Role.java` / `UserState.java` para que coincida.

### Contrasenas en texto plano (migracion automatica)

Si tu tabla trae contrasenas en texto plano (por ejemplo `'Admin123#'`,
sin hashear), **no hace falta migrarlas a mano**. `AuthService` detecta
si el valor guardado tiene el formato de hash esperado
(`saltBase64:hashBase64`); si no lo tiene, compara directamente contra el
texto plano y, si el login es correcto, re-hashea la contrasena y la
guarda de inmediato. Es decir: la primera vez que cada usuario inicia
sesion exitosamente, su contrasena queda migrada a hash en la base de
datos, sin downtime ni script aparte.

## Politica de contrasenas

Al crear (RF-01) o editar un usuario, la contrasena debe cumplir:

- Minimo 6 caracteres.
- Al menos un digito (0-9).
- Al menos una letra mayuscula.
- Al menos un caracter especial (cualquier simbolo que no sea letra ni digito).

La regla vive en una unica clase, `security/PasswordPolicy.java`, y la
usan tanto `UserService` (registro/edicion) como el dialogo de la UI
(`UserFormDialog`) para dar el mensaje de error especifico sin duplicar
la logica de validacion (SRP: es la unica "razon para cambiar" si algun
dia cambian los requisitos).

Esta politica **no** afecta el login (RF-02): los usuarios que ya existen
en la base de datos, aunque su contrasena no cumpla estos requisitos,
pueden seguir iniciando sesion con normalidad. Solo aplica al crear
cuentas nuevas o cuando alguien cambia su contrasena desde la app.

## Como se aplica cada principio SOLID

- **SRP (Responsabilidad unica):** `UserService` (registro/roles),
  `AuthService` (autenticacion) y `SQLiteUserRepository` (persistencia)
  son clases separadas a proposito, cada una con una unica razon para
  cambiar. Antes se ve en el proyecto de ejemplo `1_UnicaRazon` como el
  "sintoma" es juntar todo eso en una sola clase `Service`.

- **OCP (Abierto/cerrado):** agregar un nuevo algoritmo de hashing o un
  nuevo motor de base de datos no requiere modificar `UserService` ni
  `AuthService`; solo se crea una nueva clase que implemente
  `IPasswordHasher` o `IUserRepository`.

- **LSP (Sustitucion de Liskov):** tanto `InMemoryUserRepository` como
  `SQLiteUserRepository` cumplen exactamente el contrato de
  `IUserRepository` (mismas precondiciones/postcondiciones); se puede
  sustituir una por otra sin sorpresas en el comportamiento de los
  servicios.

- **ISP (Segregacion de interfaces):** `IUserRepository` solo tiene
  metodos relacionados con usuarios. Si el sistema crece con Preguntas,
  Revisiones, etc., cada entidad tendra su propia interfaz de
  repositorio en vez de una interfaz "gorda" compartida.

- **DIP (Inversion de dependencias):** `UserService` y `AuthService`
  reciben `IUserRepository` e `IPasswordHasher` por constructor (inyeccion
  de dependencias). Nunca hacen `new SQLiteUserRepository(...)`
  directamente, por eso `Main.java` es el unico lugar del proyecto que
  decide la implementacion concreta a usar.

## Como ejecutar

Requisitos: JDK 17+ y Maven 3.8+.

```bash
# Compilar y correr las pruebas
mvn test

# Version grafica (JavaFX) - RECOMENDADA
mvn javafx:run

# Version de consola (sin ventanas), usa SQLite, crea data.db
mvn exec:java

# Generar un jar ejecutable de la version de consola
mvn package
java -jar target/gestion-usuarios.jar
```

Al ejecutarse, `SQLiteUserRepository` crea automaticamente el archivo
`data.db` y la tabla `usuario` si no existen (mismo esquema que ya
manejabas en tu `querys.sql`: `usu_id`, `usu_usuario`, `usu_nombre`,
`usu_rol`, `usu_estado`, `password`).

## Cambiar entre memoria y SQLite

En `Main.java`, la unica linea a modificar es:

```java
// En memoria (para pruebas rapidas, no persiste nada):
IUserRepository userRepository = new InMemoryUserRepository();

// Persistente con SQLite:
IUserRepository userRepository = new SQLiteUserRepository("data.db");
```

## Siguientes pasos sugeridos

1. Reemplazar `Sha256PasswordHasher` por BCrypt (`org.mindrogo:jbcrypt` u
   otra libreria) si el proyecto pasa a produccion.
2. Agregar una capa de presentacion (consola interactiva, API REST con
   Spark/Javalin, o una UI) que consuma `UserService`/`AuthService`.
3. Si luego usas la extension SQLite de VS Code (alexcvzz) para inspeccionar
   `data.db`, recuerda hacer "Open Database" sobre el archivo para que deje
   de mostrar "No connection attached".
