package dijkstrasAlg;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinPathTest {
    private static String ANY = null;

    private void assertMinPath(String graph,
                               Integer length, String path) {
        PathFinder pf = makePathFinder(graph);
        if (length != null)
            assertEquals((int) length, pf.getLength());
        if (path != null)
            assertEquals(path, pf.getPath().toString());
    }

    private PathFinder makePathFinder(String graph) {
        PathFinder pf = new PathFinder();
        Pattern edgePattern =
                Pattern.compile("(\\D+)(\\d+)(\\D+)");
        String[] edges = graph.split(",");
        for (String edge : edges) {
            Matcher matcher = edgePattern.matcher(edge);
            if (matcher.matches()) {
                String start = matcher.group(1);
                int length = Integer.parseInt(matcher.group(2));
                String end = matcher.group(3);
                pf.addEdge(start, end, length);
            }
        }
        pf.findPath("A", "Z");
        return pf;
    }

    @Test
    public void degenerateCases() throws Exception {
        assertMinPath("", 0, "[]");   //jeden graf
        assertMinPath("A", 0, "[]");  //jeden wierzchołek
        assertMinPath("B1C", 0, "[]");//nie ma ani początku ani końca
        assertMinPath("A1C", 0, "[]");//nie ma końca
        assertMinPath("B1Z", 0, "[]");//nie ma początku
    }

    @Test
    public void oneEdge() throws Exception {
        assertMinPath("A1Z", 1, "[A, Z]");
        assertMinPath("A2Z", 2, "[A, Z]");
    }

    @Test
    public void twoEdges() throws Exception {
        assertMinPath("A1B,B1Z", 2, "[A, B, Z]");
        assertMinPath("B1Z,A1B", 2, "[A, B, Z]");
        assertMinPath("A1X,Y1Z", 0, "[]");
    }

    @Test
    public void threeEdges() throws Exception {
        assertMinPath("A2B,B3C,C4Z", 9, "[A, B, C, Z]");
        assertMinPath("B3C,C4Z,A2B", 9, "[A, B, C, Z]");
    }

    @Test
    public void OnlyOnePath() throws Exception {
        assertMinPath("A1B,B2C,C3Z,B4D,D6E", 6, "[A, B, C, Z]");
        assertMinPath("A1B,B2C,C3D,C3Z", 6, "[A, B, C, Z]");
    }

    @Test
    public void parallelPaths() throws Exception {
        assertMinPath("A1B,B2Z,A1Z", 1, "[A, Z]");
        assertMinPath("A1B,A1C,A2D,C5E,B8E,C1F,D3F,F2G,G3Z,E2G",
                7,"[A, C, F, G, Z]");
    }
}

class PathFinder {
    private List<Edge> edges = new ArrayList<>();
    private Set<String> nodeNames = new HashSet<>();
    private Map<String, Node> nodes = new HashMap<>();
    private Node endNode;

    public void findPath(String begin, String end) {
        List<String> unvisited = initializeSearch(begin, end);

        for (String node = begin;
             node != null && !node.equals(end);
             node = getNext(unvisited)) {
            unvisited.remove(node);
            visit(node);
        }

        setupEndNode(end);
    }

    private List<String> initializeSearch(String begin,
                                          String end) {
        nodeNames.add(begin);
        nodeNames.add(end);
        List<String> unvisited = new ArrayList<>(nodeNames);
        for (String node : unvisited)
            nodes.put(node, new Node(Integer.MAX_VALUE));

        nodes.get(begin).length = 0;
        return unvisited;
    }

    private void visit(String node) {
        List<Edge> neighbors = findEdges(node);
        Node curNode = nodes.get(node);
        for (Edge e : neighbors) {
            Node nbr = nodes.get(e.end);

            int newLength = curNode.length + e.length;
            if (nbr.length > newLength) {
                nbr.length = newLength;
                nbr.path = new ArrayList<String>();
                nbr.path.addAll(curNode.path);
                nbr.path.add(node);
            }
        }
    }

    private void setupEndNode(String end) {
        endNode = nodes.get(end);
        if (endNode.length != Integer.MAX_VALUE)
            endNode.path.add(end);
        else
            endNode.length = 0;
    }

    private String getNext(List<String> unvisited) {
        String minNodeName = null;
        int minLength = Integer.MAX_VALUE;

        for (String name : unvisited) {
            Node candidate = nodes.get(name);
            if (candidate.length < minLength) {
                minLength = candidate.length;
                minNodeName = name;
            }
        }
        return minNodeName;
    }

    private List<Edge> findEdges(String begin) {
        List<Edge> found = new ArrayList<>();
        for (Edge e : edges) {
            if (e.begin.equals(begin))
                found.add(e);
        }
        return found;
    }

    public int getLength() {
        return endNode.length;
    }

    public List<String> getPath() {
        return endNode.path;
    }

    public void addEdge(String start, String end, int length) {
        edges.add(new Edge(start, end, length));
        nodeNames.add(start);
        nodeNames.add(end);
    }

    private static class Edge {
        public final String begin;
        public final String end;
        public final int length;

        public Edge(String begin, String end, int length) {
            this.begin = begin;
            this.end = end;
            this.length = length;
        }
    }

    private static class Node {
        public int length;
        public List<String> path;

        public Node(int l) {
            this.length = l;
            this.path = new ArrayList<>();
        }
    }
}