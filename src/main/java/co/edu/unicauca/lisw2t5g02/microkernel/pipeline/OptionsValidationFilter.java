package co.edu.unicauca.lisw2t5g02.microkernel.pipeline;

import co.edu.unicauca.lisw2t5g02.microkernel.QuestionRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @class OptionsValidationFilter
 * @brief Filtro 2: valida cantidad y calidad de las opciones.
 * Reglas: mínimo 2 opciones, ninguna vacía, sin duplicados.
 *
 * @author Grupo LISW2 T5 G02
 */
public class OptionsValidationFilter implements QuestionFilter {

    private static final int MINIMO_OPCIONES = 2;

    @Override
    public QuestionRequest process(QuestionRequest request) throws QuestionValidationException {
        List<String> opciones = request.getOpciones();

        if (opciones == null || opciones.size() < MINIMO_OPCIONES) {
            throw new QuestionValidationException(
                    "La pregunta debe tener al menos " + MINIMO_OPCIONES + " opciones.");
        }

        Set<String> vistas = new HashSet<>();
        for (String opcion : opciones) {
            if (opcion == null || opcion.trim().isEmpty()) {
                throw new QuestionValidationException("Ninguna opción puede estar vacía.");
            }
            if (!vistas.add(opcion.trim().toLowerCase())) {
                throw new QuestionValidationException("Las opciones no pueden repetirse: '" + opcion + "'.");
            }
        }
        return request;
    }
}
