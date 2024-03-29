import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Graph implements Iterable<Integer> {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;
    private int startVertex;

    // Initialize a graph with the given number of vertices and no edges.
    public Graph(int numVertices) {
        adjLists = new LinkedList[numVertices];
        startVertex = 0;
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    public ArrayList<Integer> shortestPath(int startVertex, int endVertex) {
        HashSet<Integer> visited = new HashSet<>();
        HashMap<Integer, Integer> valsMap = new HashMap<>();
        PriorityQueue<EdgeNode> fringe = new PriorityQueue<>();
        HashMap<Integer, EdgeNode> edgeNodes = new HashMap<>();
        ArrayList<Integer> result = new ArrayList<>();


        List<Integer> currNeighbors;
        int currVertex = startVertex;
        fringe.add(new EdgeNode(currVertex,currVertex,0));
        valsMap.put(currVertex, 0);

        int i = 0;
        while (visited.size() != adjLists.length-1) {

            currVertex = fringe.poll().to;
            currNeighbors = neighbors(currVertex);

            //for every neighbor of current vertex
            for (Integer vertex : currNeighbors) {
                //if we still have not traversed that vertex
                if (!visited.contains(vertex)) {

                        valsMap.put(vertex, valsMap.get(currVertex) + (Integer)getEdge(currVertex, vertex).edgeInfo);
                        EdgeNode e = new EdgeNode(currVertex, vertex,
                                (Integer)getEdge(currVertex, vertex).edgeInfo);
                        fringe.add(e);
                        edgeNodes.put(vertex, e);
                }
            }
            visited.add(currVertex);
             i++;
        }


        Integer last = endVertex;
        while (!result.contains(startVertex)){
            result.add(last);
            last = edgeNodes.get(last) != null ? edgeNodes.get(last).from : null;
        }

        Collections.reverse(result);
        return result;
    }

    private void mapPrint(HashMap valsMap){
        Iterator it = valsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove();
        }
    }


    public Edge getEdge(int u, int v) {
        for (Edge e : adjLists[u]) {
            if (e.from == u && e.to == v) {
                return e;
            }
        }
        return null;
    }


    private class EdgeNode implements Comparable{
        int from;
        int to;
        int weight;

        public EdgeNode(int from, int to, int weight){
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public EdgeNode(int pred, Edge e){
            this.from = e.from;
            this.to = e.to;
            this.weight = (Integer) e.edgeInfo;
        }


        @Override
        public int compareTo(Object o) {
            return this.weight > ((EdgeNode) o).weight ? 1 : this.weight < ((EdgeNode) o).weight ? -1 : 0;
        }
    }

    // Change the vertex the iterator will start DFS from
    public void setStartVertex(int v) {
        if (v < vertexCount && v >= 0) {
            startVertex = v;
        } else {
            throw new IllegalArgumentException("Cannot set iteration start vertex to " + v + ".");
        }
    }


    // Add to the graph a directed edge from vertex v1 to vertex v2.
    public void addEdge(int v1, int v2) {
        addEdge(v1, v2, null);
    }

    // Add to the graph an undirected edge from vertex v1 to vertex v2.
    public void addUndirectedEdge(int v1, int v2) {
        addUndirectedEdge(v1, v2, null);
    }

    // Add to the graph a directed edge from vertex v1 to vertex v2,
    // with the given edge information. If the edge already exists,
    // replaces the current edge with a new edge with edgeInfo.
    public void addEdge(int v1, int v2, Object edgeInfo) {
        Edge temp = new Edge(v1, v2, edgeInfo);
        for (Edge e : adjLists[v1]) {
            if (e.equals(temp)) {
                adjLists[v1].remove(e);
                adjLists[v1].add(temp);
                return;
            }
        }
        adjLists[v1].add(temp);
    }

    // Add to the graph an undirected edge from vertex v1 to vertex v2,
    // with the given edge information. If the edge already exists,
    // replaces the current edge with a new edge with edgeInfo.
    public void addUndirectedEdge(int v1, int v2, Object edgeInfo) {
        addEdge(v1, v2, edgeInfo);
        addEdge(v2, v1, edgeInfo);
    }

    // Return true if there is an edge from vertex "from" to vertex "to";
    // return false otherwise.
    public boolean isAdjacent(int from, int to) {
        for (Edge e : adjLists[from]) {
            if (e.from == from && e.to == to) {
                return true;
            }
        }
        return false;
    }

    // Returns a list of all the neighboring  vertices 'u'
    // such that the edge (VERTEX, 'u') exists in this graph.
    public List<Integer> neighbors(int vertex) {
        return adjLists[vertex].stream().map(e -> e.to).collect(Collectors.toList());
    }

    // Return the number of incoming vertices for the given vertex,
    // i.e. the number of vertices v such that (v, vertex) is an edge.
    public int inDegree(int vertex) {
        int count = 0;
        for (int i = 0; i < adjLists.length; i++) {
            if (!(i == vertex)) {
                if (isAdjacent(i, vertex)) {
                    count++;
                }
            }
        }
        return count;
    }

    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

    // A class that iterates through the vertices of this graph, starting with a given vertex.
    // Does not necessarily iterate through all vertices in the graph: if the iteration starts
    // at a vertex v, and there is no path from v to a vertex w, then the iteration will not
    // include w
    private class DFSIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        private HashSet<Integer> visited;

        public DFSIterator(Integer start) {
            fringe = new Stack<>();
            visited = new HashSet<>();
            fringe.push(start);
            visited.add(start);
            //visited.add(start);
        }

        public boolean hasNext() {
            if (!fringe.isEmpty()) {
                return true;
            }
            return false;
        }


        public Integer next() {
            int ret = fringe.pop();
            //visited.add(ret);
            List<Integer> _neighbors = neighbors(ret);
            _neighbors.sort((v1, v2) -> v1 < v2 ? -1 : v1 > v2 ? +1 : 0);

            for (Integer i : _neighbors) {
                if (!visited.contains(i)) {
                    fringe.push(i);
                    visited.add(i);
                }
            }
            return ret;
        }

        //ignore this method
        public void remove() {
            throw new UnsupportedOperationException(
                    "vertex removal not implemented");
        }

    }

    // Return the collected result of iterating through this graph's
    // vertices as an ArrayList, starting from STARTVERTEX.
    public ArrayList<Integer> visitAll(int startVertex) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(startVertex);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    // Returns true iff there exists a path from STARVETEX to
    // STOPVERTEX. Assumes both STARTVERTEX and STOPVERTEX are
    // in this graph. If STARVERTEX == STOPVERTEX, returns true.
    public boolean pathExists(int startVertex, int stopVertex) {
        if (startVertex == stopVertex) {
            return true;
        }

        return (visitAll(startVertex).contains(stopVertex));
    }


    // Returns the path from startVertex to stopVertex.
    // If no path exists, returns an empty arrayList.
    // If startVertex == stopVertex, returns a one element arrayList.
    public ArrayList<Integer> path(int startVertex, int stopVertex) {

        ArrayList<Integer> visited = new ArrayList<Integer>();
        ArrayList<Integer> result = new ArrayList<Integer>();

        // if path does not exist or the start==end, then return result
        if (!pathExists(startVertex, stopVertex)) {
            return result;
        } else if (startVertex == stopVertex) {
            result.add(startVertex);
            return result;
        }

        // add all items on paths to visited.
        Iterator<Integer> iter = new DFSIterator(startVertex);
        while (iter.hasNext()) {
            Integer val = iter.next();
            visited.add(val);
            if (val == stopVertex) {
                result.add(stopVertex);
                break;
            }
        }

        for (int i = visited.size() - 2; i >= 0; i--) {
            Integer v = visited.get(i);

            if (isAdjacent(v, result.get(result.size() - 1))) {
                result.add(v);
            }
        }

        Collections.reverse(result);

        return result;

    }

    public ArrayList<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    private class TopologicalIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        //create inDegree array for each vertex
        private Integer currentInDegree[];

        public TopologicalIterator() {
            fringe = new Stack<Integer>();
            currentInDegree = new Integer[adjLists.length];
            //initialize inDegree arr for the current in degree of each vertex.
            for (int i = 0; i < adjLists.length; i++) {
                int inD = inDegree(i);
                currentInDegree[i] = inD;
                //add source (0 in degree) to fringe
                if (inD == 0) {
                    fringe.push(i);
                }
            }
        }

        public boolean hasNext() {
            return !fringe.isEmpty();
        }

        public Integer next() {
            //pop source
            int ret = fringe.pop();

            //update all of the sort's neighbors' degrees to be -1
            //since we processed the sort.
            for (Integer i : neighbors(ret)) {
                currentInDegree[i]--;
                //push 0 in degrees to fringe
                if (currentInDegree[i] == 0) {
                    fringe.push(i);
                }
            }
            return ret;
        }

        public void remove() {
            throw new UnsupportedOperationException(
                    "vertex removal not implemented");
        }

    }

    private class Edge {

        private Integer from;
        private Integer to;
        private Object edgeInfo;

        public Edge(int from, int to, Object info) {
            this.from = new Integer(from);
            this.to = new Integer(to);
            this.edgeInfo = info;
        }

        public Integer to() {
            return to;
        }

        public Object info() {
            return edgeInfo;
        }

        public String toString() {
            return "(" + from + "," + to + ",dist=" + edgeInfo + ")";
        }

        //added method
        @Override
        public boolean equals(Object obj) {
            return this.toString().equals(obj.toString());
        }



}

    public static void main(String[] args) {
        Graph g1 = new Graph(5);
        g1.addEdge(0, 1, 10);
        g1.addEdge(0, 4, 100);
        g1.addEdge(0, 3, 30);
        g1.addEdge(1, 2, 50);
        g1.addEdge(2, 4, 10);
        g1.addEdge(3, 2, 20);
        g1.addEdge(3, 4, 60);
        System.out.println(g1.shortestPath(0, 4));
        //g1.neighbors(4);


//        ArrayList<Integer> result;
//
//        Graph g1 = new Graph(5);
//        g1.addEdge(0, 1);
//        g1.addEdge(0, 2);
//        g1.addEdge(0, 4);
//        g1.addEdge(1, 2);
//        g1.addEdge(2, 0);
//        g1.addEdge(2, 3);
//        g1.addEdge(4, 3);
//        System.out.println("Traversal starting at 0");
//        result = g1.visitAll(0);
//        Iterator<Integer> iter;
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Traversal starting at 2");
//        result = g1.visitAll(2);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Traversal starting at 3");
//        result = g1.visitAll(3);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Traversal starting at 4");
//        result = g1.visitAll(4);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Path from 0 to 3");
//        result = g1.path(0, 3);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Path from 0 to 4");
//        result = g1.path(0, 4);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Path from 1 to 3");
//        result = g1.path(1, 3);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Path from 1 to 4");
//        result = g1.path(1, 4);
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
//        System.out.println();
//        System.out.println();
//        System.out.println("Path from 4 to 0");
//        result = g1.path(4, 0);
//        if (result.size() != 0) {
//            System.out.println("*** should be no path!");
//        }
//
//        Graph g2 = new Graph(5);
//        g2.addEdge(0, 1);
//        g2.addEdge(0, 2);
//        g2.addEdge(0, 4);
//        g2.addEdge(1, 2);
//        g2.addEdge(2, 3);
//        g2.addEdge(4, 3);
//        System.out.println();
//        System.out.println();
//        System.out.println("Topological sort");
//        result = g2.topologicalSort();
//        iter = result.iterator();
//        while (iter.hasNext()) {
//            System.out.println(iter.next() + " ");
//        }
    }

}
