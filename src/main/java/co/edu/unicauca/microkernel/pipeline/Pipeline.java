package co.edu.unicauca.microkernel.pipeline;

import java.util.List;

public class Pipeline<T> {

    private List<Filter<T>> filters;

    public Pipeline(List<Filter<T>> filters) {
        this.filters = filters;
    }

    public T process(T input) {

        T result = input;

        for (Filter<T> filter : filters) {
            result = filter.process(result);
        }

        return result;
    }
}
