# Cambios (correcciones tras la prueba de usabilidad)

- **CreateQuestionView**: valida campos vacíos antes de guardar, los resalta en rojo y muestra diálogos (antes solo una etiqueta pequeña). Limpia el formulario tras guardar.
- **GUIQuestions**: el botón "NUEVA PREGUNTA" (que dejaba todo inactivo) ahora abre CreateQuestionView. MODIFICAR está siempre disponible y avisa si la pregunta no es BORRADOR (criterio 8). Aviso de "sin resultados" solo al filtrar. Pista contextual de qué hacer. Se refresca por Observer.
- **Administrador**: "Consultar preguntas" lista TODAS las preguntas (solo lectura) en vez de filtrar por el id del admin.
- **AdminReviewView**: se registra como Observer (y se desregistra al cerrar), botón REFRESCAR, mensaje si no hay pendientes, y muestra advertencias si el correo falla en vez de decir siempre "correo enviado".
- **QuestionValidator**: única pregunta directa (criterio 4) y longitud máxima de opciones (criterio 8).
- **Repositorio/Servicio/Controlador**: consultas paginadas y filtradas sobre todas las preguntas; orden por inserción (rowid).
