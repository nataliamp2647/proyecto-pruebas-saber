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

## Ejecución con Maven

```bash
mvn clean package
mvn exec:java
```

También puede ejecutarse el JAR generado en `target/` después de empaquetar.
