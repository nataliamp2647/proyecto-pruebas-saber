# Banco de Preguntas Saber PRO — Taller 5 (Microkernel + Tuberías y Filtros)

Aplicación Java SE de escritorio (Swing + Maven + SQLite) que unifica los
talleres del curso en un solo proyecto:

| Taller | Aporte | Dónde vive |
|---|---|---|
| Taller 2 | Gestión de usuarios y autenticación con principios SOLID | `domain/user`, `security`, `access` |
| Taller 4 | Gestión de preguntas con arquitectura en capas, MVC y Observer | `domain/question`, `infra`, `presentation` |
| **Taller 5** | **Microkernel + Tuberías y Filtros** | `microkernel` |

## Arquitectura en capas

```
presentation/   LoginView, MainMenuView, UserAdminView, UserFormDialog,
                GUIQuestions, GUIObserver1, GUIObserver2, MicrokernelView
domain/user     User, Role, UserState, IUserRepository, UserService, AuthService
domain/question Question, QuestionDistractors, QuestionRepository, QuestionService
microkernel/    QuestionMicrokernel, QuestionPlugin, QuestionRequest
   pipeline/    QuestionFilter + los 4 filtros + QuestionPipeline
   plugins/     MultipleChoice, Case, Multimedia
access/         SQLiteUserRepository, InMemoryUserRepository, QuestionImplRepository
infra/          Observer, Subject
security/       IPasswordHasher, Sha256PasswordHasher, PasswordPolicy
```

`Application` es el único punto que decide implementaciones concretas
(SQLite, SHA-256) y las inyecta: ninguna otra clase depende de una
implementación (DIP).

## Patrón Microkernel

`QuestionMicrokernel` es el núcleo. **Nunca** hace `new XxxPlugin()` ni
importa una clase de plugin concreta: lee `src/main/resources/plugins.properties`
e instancia cada clase por **reflexión** (`Class.forName` +
`getDeclaredConstructor().newInstance()`), verificando que implemente
`QuestionPlugin`.

```properties
multiple-choice=co.edu.unicauca.lisw2t5g02.microkernel.plugins.MultipleChoiceQuestionPlugin
case-analysis=co.edu.unicauca.lisw2t5g02.microkernel.plugins.CaseQuestionPlugin
multimedia=co.edu.unicauca.lisw2t5g02.microkernel.plugins.MultimediaQuestionPlugin
```

Para agregar un tipo de pregunta nuevo basta con crear la clase y añadir
una línea aquí: **el núcleo no se modifica**.

## Patrón Tuberías y Filtros

`MultipleChoiceQuestionPlugin` implementa el pipeline exigido. La
solicitud pasa por cuatro filtros en cadena y, si alguno la rechaza, la
generación se detiene ahí:

```
ContentValidationFilter    → texto no vacío, longitud mínima, formato
OptionsValidationFilter    → mínimo 2 opciones, ninguna vacía, sin duplicados
ClassificationFilter       → asigna competencia y nivel de dificultad
CorrectAnswerValidationFilter → respuesta existe, pertenece a las opciones, datos consistentes
```

## Integración entre talleres

- El login (Taller 2) controla el acceso: el menú muestra la
  administración de usuarios solo al rol **Administrador**, y la
  generación por plugins a **Administrador** y **Autor de preguntas**.
- Las preguntas generadas por el microkernel se **persisten en la misma
  base SQLite** del Taller 4 (`QuestionRepository.guardar`), por lo que
  aparecen en el comboBox de gestión de preguntas y se reflejan en las
  vistas observadoras de estadísticas y gráfico.

## Cómo ejecutar

```bash
mvn compile exec:java
```

Usuario administrador semilla (se crea solo si la base está vacía):

```
Usuario:     admin
Contraseña:  Admin123#
```

## Cómo correr las pruebas

```bash
mvn test
```

79 pruebas en 11 clases: los 4 filtros por separado, el pipeline
completo, los 3 plugins, el núcleo (incluida la carga real por reflexión,
el rechazo de clases que no implementan `QuestionPlugin` y la
persistencia delegada al repositorio), más las pruebas heredadas de los
talleres 2 y 4.

## Repositorio

<!-- TODO: agregar el enlace al repositorio de GitHub del grupo -->
