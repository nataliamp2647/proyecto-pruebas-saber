# PatronCapasMicroPatronMVC

Proyecto Java de gestión de preguntas con arquitectura monolítica en capas,
SQLite y el patrón Observer.

## Estructura de capas

```text
presentation
    GUIQuestions.java
    GUIObserver1.java
    GUIObserver2.java

domain
    Question.java
    QuestionDistractors.java
    QuestionService.java
    QuestionRepository.java

access
    QuestionImplRepository.java

infra
    Observer.java
    Subject.java
```

`Application.java` es el punto de entrada/composición de dependencias y no forma
parte de una capa funcional.

## Responsabilidad de cada capa

- **presentation:** ventanas Swing y visualización.
- **domain:** entidades, contrato del repositorio y reglas/casos de uso.
- **access:** persistencia JDBC sobre SQLite.
- **infra:** infraestructura transversal del patrón Observer.

La base de datos `preguntas.db` se crea automáticamente al ejecutar la aplicación.
La primera ejecución crea las tablas `pregunta` y `opcion` y carga seis preguntas de ejemplo.

## Patrón MVC

- **Modelo:** `Question`, `QuestionDistractors`, `QuestionService`, `QuestionRepository`.
  Representan la información del sistema y las operaciones relacionadas con las
  preguntas. El acceso a la persistencia se realiza mediante `QuestionImplRepository`.
- **Vista:** `GUIQuestions`, `GUIObserver1`, `GUIObserver2`. Cada una tiene una
  responsabilidad específica de representación (formulario principal, estadísticas
  y gráfico de pastel).
- **Controlador:** este proyecto **no tiene una clase Controlador independiente**.
  `GUIQuestions` asume simultáneamente el rol de Vista y de Controlador: sus
  `ActionListener` reciben la interacción del usuario (seleccionar/cargar pregunta,
  actualizar estado) y deciden qué operación invocar en `QuestionService`, sin
  contener lógica de negocio propia. Esta fusión de responsabilidades se consideró
  aceptable dado el alcance del taller; en una iteración futura podría extraerse un
  Controlador explícito para desacoplar completamente la captura de eventos de
  Swing del renderizado de la vista.

Flujo de una acción típica (ej. actualizar el estado de una pregunta):

```text
Usuario -> GUIQuestions (Vista/Controlador)
        -> QuestionService (Modelo)
        -> QuestionRepository (interfaz)
        -> QuestionImplRepository (Modelo / acceso a datos)
        -> SQLite
```

Tras guardar el cambio, `QuestionService` notifica (patrón Observer) a
`GUIObserver1` y `GUIObserver2`, que se actualizan sin que el servicio conozca
sus clases concretas.

## Ejecución con Maven

```bash
mvn clean package
mvn exec:java
```

También puede ejecutarse el JAR generado en `target/` después de empaquetar.
