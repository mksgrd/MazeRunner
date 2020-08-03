package maze;


import java.io.Serializable;
import java.util.Objects;

public class Edge<T extends Serializable> implements Comparable<Edge<T>>, Serializable {
    private final T from;
    private final T to;
    private final int weight;

    public Edge(T from, T to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Edge(T from, T to) {
        this(from, to, 0);
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return weight == edge.weight &&
                Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public int compareTo(Edge<T> o) {
        return Integer.compare(weight, o.weight);
    }
}
