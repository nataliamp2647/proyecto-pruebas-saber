package co.edu.unicauca.lisw2t5g02.domain.question;

import java.util.ArrayList;
import java.util.List;

/** Entidad de dominio de una pregunta Saber Pro. */
public class Question {
    public enum EstadoPregunta {
        BORRADOR("Borrador"), PENDIENTE_REVISION("Pendiente de revisión"), ELIMINADA("Eliminada");
        private final String etiqueta;
        EstadoPregunta(String etiqueta){this.etiqueta=etiqueta;}
        public String getEtiqueta(){return etiqueta;}
        @Override public String toString(){return etiqueta;}
        public static EstadoPregunta fromEtiqueta(String e){
            for(EstadoPregunta x:values()) if(x.etiqueta.equalsIgnoreCase(e)) return x;
            throw new IllegalArgumentException("Estado desconocido: "+e);
        }
    }
    private String id, nombre, contexto, preguntaDirecta, justificacion, bibliografia, competencia, tema, subtema, nivelDificultad;
    private String respuestaCorrecta;
    private EstadoPregunta estado;
    private int autorId;
    private List<QuestionDistractors> opciones = new ArrayList<>();
    public Question(){}
    public Question(String id,String nombre,String enunciado,String respuestaCorrecta,EstadoPregunta estado){
        this.id=id;this.nombre=nombre;this.preguntaDirecta=enunciado;this.respuestaCorrecta=respuestaCorrecta;this.estado=estado;
    }
    public void agregarOpcion(QuestionDistractors o){opciones.add(o);}
    public String getId(){return id;} public void setId(String v){id=v;}
    public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public String getEnunciado(){return preguntaDirecta;} public void setEnunciado(String v){preguntaDirecta=v;}
    public String getContexto(){return contexto;} public void setContexto(String v){contexto=v;}
    public String getPreguntaDirecta(){return preguntaDirecta;} public void setPreguntaDirecta(String v){preguntaDirecta=v;}
    public String getJustificacion(){return justificacion;} public void setJustificacion(String v){justificacion=v;}
    public String getBibliografia(){return bibliografia;} public void setBibliografia(String v){bibliografia=v;}
    public String getCompetencia(){return competencia;} public void setCompetencia(String v){competencia=v;}
    public String getTema(){return tema;} public void setTema(String v){tema=v;}
    public String getSubtema(){return subtema;} public void setSubtema(String v){subtema=v;}
    public String getNivelDificultad(){return nivelDificultad;} public void setNivelDificultad(String v){nivelDificultad=v;}
    public String getRespuestaCorrecta(){return respuestaCorrecta;} public void setRespuestaCorrecta(String v){respuestaCorrecta=v;}
    public EstadoPregunta getEstado(){return estado;} public void setEstado(EstadoPregunta v){estado=v;}
    public int getAutorId(){return autorId;} public void setAutorId(int v){autorId=v;}
    public List<QuestionDistractors> getOpciones(){return opciones;} public void setOpciones(List<QuestionDistractors> v){opciones=v;}
    @Override public String toString(){return id+" - "+nombre;}
}
