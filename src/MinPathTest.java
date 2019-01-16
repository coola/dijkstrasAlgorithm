import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        Pattern edgePattern = Pattern.compile("(\\D+)(\\d+)(\\D+)");
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
        assertMinPath("A2Z", 2, "[A, Z]");
    }

    @Test
    public void twoEdges() throws Exception {
        assertMinPath("A1B,B1Z", 2, "[A, B, Z]");
        assertMinPath("B1Z,A1B", 2, "[A, B, Z]");
        assertMinPath("A1X,Y1Z", 0, "[]");
    }
}

class PathFinder {
    private List<Edge> edges = new ArrayList<>();
    private List<String> path = new ArrayList<>();
    private int length;

    public PathFinder() {
    }

    public void findPath(String begin, String end) {
        List<String> p = new ArrayList<>();
        int l = 0;
        p.add(begin);

        for (Edge e = findEdge(begin);
             e != null; e = findEdge(e.end)) {
            p.add(e.end);
            l += e.length;
            if (e.end.equals(end)) {
                length = l;
                path = p;
                return;
            }
        }
    }

    private Edge findEdge(String begin) {
        for (Edge e : edges) {
            if (e.begin.equals(begin))
                return e;
        }
        return null;
    }

    public int getLength() {
        return length;
    }

    public List<String> getPath() {
        return path;
    }

    public void addEdge(String start, String end, int length) {
        edges.add(new Edge(start, end, length));
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
}