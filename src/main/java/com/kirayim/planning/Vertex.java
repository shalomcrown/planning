package com.kirayim.planning;

import java.util.Objects;

public class Vertex {
    public Vertex parent = null;
    public long state;
    public Operator howWeGotHere = null;
    public double value = Double.MAX_VALUE;

    public String humanReadable;

    public Vertex(long state, Vertex parent, double value, Operator op) {
        this.parent = parent;
        this.state = state;
        this.value = value;
        this.howWeGotHere = op;
    }

    // ==============================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        return Objects.equals(state, vertex.state);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(state);
    }

    @Override
    public String toString() {
        return "Vertex [" +
                "state=" + state +
                ", howWeGotHere=" + howWeGotHere +
                ", value=" + value +
                ']';
    }
}
