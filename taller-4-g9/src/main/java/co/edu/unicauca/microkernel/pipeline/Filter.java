package co.edu.unicauca.microkernel.pipeline;

public interface Filter<T> {

    T process(T input);
}