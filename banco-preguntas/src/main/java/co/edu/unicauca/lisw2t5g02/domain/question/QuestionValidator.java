package co.edu.unicauca.lisw2t5g02.domain.question;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Structural rules for Saber Pro single-answer multiple-choice questions. */
public final class QuestionValidator {
    public void validate(Question q) {
        if (q == null) throw new IllegalArgumentException("La pregunta es obligatoria.");
        required("Nombre", q.getNombre()); required("Contexto", q.getContexto());
        required("Pregunta directa", q.getPreguntaDirecta()); required("Justificación", q.getJustificacion());
        required("Bibliografía", q.getBibliografia()); required("Competencia", q.getCompetencia());
        required("Tema", q.getTema()); required("Subtema", q.getSubtema());
        required("Nivel de dificultad", q.getNivelDificultad());
        if (countChar(q.getPreguntaDirecta(), '?') > 1 || countChar(q.getPreguntaDirecta(), '¿') > 1)
            throw new IllegalArgumentException("Debe existir una única pregunta directa.");
        List<QuestionDistractors> options = q.getOpciones();
        if (options == null || options.size() != 5)
            throw new IllegalArgumentException("La pregunta debe incluir una respuesta correcta y exactamente cuatro distractores.");
        Set<String> unique = new HashSet<>();
        boolean correctFound = false;
        for (QuestionDistractors option : options) {
            if (option == null || option.getTexto() == null || option.getTexto().isBlank())
                throw new IllegalArgumentException("Las cinco opciones deben tener texto.");
            if (option.getTexto().trim().length() > MAX_OPTION_LENGTH)
                throw new IllegalArgumentException("La opción " + option.getLetra() + " supera los " + MAX_OPTION_LENGTH + " caracteres permitidos.");
            String normalized = option.getTexto().trim().toLowerCase(Locale.ROOT);
            if (!unique.add(normalized)) throw new IllegalArgumentException("Las opciones no pueden estar repetidas.");
            if (normalized.contains("todas las anteriores") || normalized.contains("ninguna de las anteriores"))
                throw new IllegalArgumentException("No se permiten las expresiones ‘Todas las anteriores’ ni ‘Ninguna de las anteriores’.");
            if (option.getLetra() != null && option.getLetra().equalsIgnoreCase("" + q.getRespuestaCorrecta())) correctFound = true;
            if (option.getTexto().equals(q.getRespuestaCorrecta())) correctFound = true;
        }
        if (q.getRespuestaCorrecta() == null || q.getRespuestaCorrecta().isBlank() || !correctFound)
            throw new IllegalArgumentException("La respuesta correcta debe corresponder a una de las cinco opciones.");
    }

    private static final int MAX_OPTION_LENGTH = 250;

    private static int countChar(String text, char c) {
        int n = 0;
        if (text != null) for (int i = 0; i < text.length(); i++) if (text.charAt(i) == c) n++;
        return n;
    }

    private void required(String label, String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("El campo " + label + " es obligatorio.");
    }
}
