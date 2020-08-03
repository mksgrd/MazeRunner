package maze;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Graph<T extends Serializable> implements Serializable {
    private final Map<T, List<Edge<T>>> adjList;

    public Graph() {
        adjList = new HashMap<>();
    }

    private boolean hasEdge(T from, T to) {
        for (Edge<T> edge : adjList.getOrDefault(from, Collections.emptyList())) {
            if (edge.getTo().equals(to)) {
                return true;
            }
        }
        return false;
    }

    private void addDirectedEdge(T from, T to, int weight) {
        if (!hasEdge(from, to)) {
            adjList.computeIfAbsent(from, key -> new LinkedList<>());
            List<Edge<T>> edges = adjList.get(from);
            edges.add(new Edge<>(from, to, weight));
        }
    }

    public void addEdge(T from, T to, int weight) {
        addDirectedEdge(from, to, weight);
        addDirectedEdge(to, from, weight);
    }

    public void addEdge(T from, T to) {
        addEdge(from, to, 0);
    }

    public void addEdge(Edge<T> edge) {
        addEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
    }

    public List<Edge<T>> toEdgesList() {
        return adjList.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Graph<T> findMst() {
        Graph<T> mst = new Graph<>();

        Map<T, Integer> minEdgeWeight = new HashMap<>();
        Map<T, T> prVertex = new HashMap<>();
        Set<T> used = new HashSet<>();
        Queue<Edge<T>> queue = new PriorityQueue<>();

        T init = adjList.keySet().iterator().next();
        queue.add(new Edge<>(init, init));

        for (int i = 0; i < adjList.size(); ++i) {
            T from = queue.remove().getTo();
            used.add(from);
            if (prVertex.containsKey(from)) {
                mst.addEdge(prVertex.get(from), from);
            }
            for (Edge<T> edge : adjList.get(from)) {
                if (used.contains(edge.getTo())) {
                    continue;
                }
                int curWeight = minEdgeWeight.getOrDefault(edge.getTo(), Integer.MAX_VALUE);
                if (edge.getWeight() < curWeight) {
                    queue.remove(new Edge<>(prVertex.get(edge.getTo()), edge.getTo(), curWeight));
                    minEdgeWeight.put(edge.getTo(), edge.getWeight());
                    prVertex.put(edge.getTo(), from);
                    queue.add(new Edge<>(from, edge.getTo(), edge.getWeight()));
                }
            }
        }
        return mst;
    }

    public List<Edge<T>> findPath(T from, T to) {
        List<Edge<T>> path = new LinkedList<>();
        Map<T, T> prVertex = new HashMap<>();
        Set<T> used = new HashSet<>();
        Queue<T> queue = new ArrayDeque<>();
        queue.add(from);
        used.add(from);
        while (!queue.isEmpty()) {
            T u = queue.remove();
            for (Edge<T> edge : adjList.get(u)) {
                if (!used.contains(edge.getTo())) {
                    used.add(edge.getTo());
                    prVertex.put(edge.getTo(), u);
                    queue.add(edge.getTo());
                }
            }
        }
        T cur = to;
        while (prVertex.get(cur) != null) {
            path.add(new Edge<>(prVertex.get(cur), cur));
            cur = prVertex.get(cur);
        }
        return path;
    }
}
