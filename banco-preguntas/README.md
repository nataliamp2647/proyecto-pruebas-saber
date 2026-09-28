# Banco de Preguntas Saber Pro

Aplicación Java de escritorio (Swing), monolítica y organizada en tres capas para las historias del primer corte.

## Arquitectura

```text
presentation/  Vistas Swing y controladores MVC (AuthController, QuestionController)
domain/        Entidades, casos de uso, interfaces de repositorio y QuestionValidator
access/        Implementaciones SQLite de usuarios y preguntas
security/      IPasswordHasher, Argon2PasswordHasher, PasswordPolicy
infra/         Subject/Observer para actualización de vistas
```

Las vistas envían las acciones al controlador; los servicios coordinan reglas de negocio; los repositorios encapsulan SQLite. La aplicación y sus dependencias se componen en `Application`.

## Historias implementadas

- Crear y validar preguntas de selección múltiple con una respuesta correcta y cuatro distractores.
- Listar, filtrar por estado, paginar, consultar y editar preguntas en borrador.
- Cambiar de BORRADOR a PENDIENTE DE REVISIÓN tras validar la estructura.
- Asignar uno o más revisores a preguntas pendientes y enviar notificaciones SMTP.

`QuestionValidator` centraliza campos obligatorios, cinco opciones, opciones duplicadas, frases prohibidas y pertenencia de la respuesta correcta.

## Patrones y principios

- **MVC:** `CreateQuestionView`/`LoginView` delegan acciones en `QuestionController`/`AuthController`.
- **Observer (GoF):** `QuestionService` notifica cambios a vistas observadoras para refrescar estadísticas.
- **Strategy (GoF):** `AuthService` depende de `IPasswordHasher`; `Argon2PasswordHasher` selecciona el algoritmo sin acoplar el servicio al proveedor.
- **SOLID/DIP:** servicios consumen interfaces de repositorio y hashing; `Application` ensambla implementaciones.

Se retiraron el Microkernel, sus plugins y Tuberías y Filtros. Las reglas de validación son ahora independientes en `QuestionValidator`.

## Seguridad de contraseñas

El almacenamiento nuevo usa **Argon2id** (3 iteraciones, 64 MiB, paralelismo 1). Los hashes previos SHA-256 con sal se verifican para mantener el acceso y se reemplazan por Argon2id al siguiente inicio de sesión correcto. Las contraseñas legacy en texto plano también se migran en ese inicio de sesión.

## Configuración de correo SMTP

Antes de asignar revisores, configura estas variables de entorno en la máquina que ejecuta la aplicación:

```text
SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_USER=cuenta@example.com
SMTP_PASSWORD=contraseña-de-aplicación
SMTP_FROM=cuenta@example.com   (opcional; por defecto SMTP_USER)
```

El envío usa autenticación SMTP con STARTTLS. Sin configuración el sistema reporta el error y no simula un correo enviado. `notificaciones.log` registra solo entregas exitosas.
Los correos de los usuarios asignados también deben ser direcciones reales; los usuarios semilla usan direcciones `.local` de demostración.

## Ejecutar

Requiere JDK 21 y Maven.

```bash
mvn test
mvn compile exec:java
```

## Usuarios de demostración

- Administrador: `admin` / `Admin123#`
- Autor: `autor` / `Autor123#`
- Revisor: `revisor` / `Revisor123#`

La aplicación crea las bases SQLite en el directorio de ejecución. Para las notificaciones se necesita SMTP válido y accesible.
