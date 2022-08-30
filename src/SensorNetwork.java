import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SensorNetwork {

    static HashMap<Integer,Node> nodesList = new HashMap<>();
    static double width, height;
    boolean connected = true;
    static int TL, numOfNodes, numOfDataNodes, numOfStorageNodes, dataPackets, storageCapacity, DN, SN, k;
    static HashMap<Integer,HashSet<Integer>> adjList = new HashMap<>();
    static HashMap<Integer, Boolean> explored = new HashMap<>();
    static List<Set<Integer>> connectedNodes = new ArrayList<>();
    static List<Integer> dataNodes = new ArrayList<>();
    static HashMap<HashSet<Integer>, Double> shortPathDistList = new HashMap<>();
    static List<Edge> mimSpanDistList = new ArrayList<>();
    static HashSet<Integer> temp = new HashSet<>();
    static List<Integer> s = new ArrayList<>();
    static double[] listOfDistance = new double[numOfNodes + 1] ;

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        SensorNetwork sensor = new SensorNetwork();

        Scanner keyboard = new Scanner(System.in);

        do {

            adjList.clear();
            explored.clear();
            connectedNodes.clear();
            dataNodes.clear();
            shortPathDistList.clear();
            temp.clear();

            if(numOfDataNodes * dataPackets > numOfStorageNodes * storageCapacity){
                System.out.println("\nThere is not enough storage in the network \n\n ---Try Again---\n");

            }else if(!sensor.connected){
                System.out.println("\n\n ---Try Again---\n");
            }

            System.out.println("Enter Width: \t");
            width = keyboard.nextDouble();

            System.out.println("\nEnter Height: \t");
            height = keyboard.nextDouble();

            System.out.println("\nEnter the num of nodes: \t");
            numOfNodes = keyboard.nextInt();

            System.out.println("\nEnter TL: \t");
            TL = keyboard.nextInt();

            System.out.println("\nEnter number of Data Nodes");
            numOfDataNodes = keyboard.nextInt();

            System.out.println("\nEnter Data packets for Data Nodes");
            dataPackets = keyboard.nextInt();

            System.out.println("\nStorage capacity of storage node");
            storageCapacity = keyboard.nextInt();

            numOfStorageNodes = numOfNodes - numOfDataNodes;

            sensor.createNode();

            sensor.createAdjacentList();

            sensor.checkConnectivity();
        } while (!sensor.connected || (numOfDataNodes * dataPackets > numOfStorageNodes * storageCapacity));

        sensor.printNodes();

        sensor.drawGraph();
        do {
            System.out.println("\nEnter DN you wish to check");
            DN = keyboard.nextInt();
        }while (!dataNodes.contains(DN));

        System.out.println("\nEnter SN you wish to check");
        SN = keyboard.nextInt();

        System.out.println("\nEnter which algorithm you wish to use ( 0 for Dijkstra’s shortest path algorithm, " +
                "1 for Bellman-Ford dynamic programming algorithm, " +
                "\nand 2 and for finding a shortest path between them with k edges )");
        int method = keyboard.nextInt();

        sensor.printDist();

        switch (method){
            case 0:
                sensor.DijkstraAlgorithm();
                sensor.calcEnergyCostDJAlgo();
                break;
            case 1:
                sensor.bellmanFordAlgo();
                sensor.calcEnergyCostPrmAlgo();
                break;
            case 2:
                System.out.println("Enter minimum k edges required");
                k = keyboard.nextInt();
                int prv[] = new int[numOfNodes + 1];
                double result = sensor.smPathWithKEdge(prv);
                while(result == Integer.MAX_VALUE){
                    k++;
                    result = sensor.smPathWithKEdge(prv);
                }

                System.out.println("Total Distance travel is: " + result);
                sensor.calcEnergyCostKEdge(result);
                //sensor.populateS(prv);
        }

        sensor.drawGraphPath();


        long endTime = System.nanoTime();
        System.out.println("\n\nTook "+(endTime - startTime)/1000000000 + " s");

        // end of main
    }

    void createNode(){
        double x,y;

        for (int i = 1; i <= numOfNodes; i++){
            x = ThreadLocalRandom.current().nextDouble(0,width);
            y = ThreadLocalRandom.current().nextDouble(0,height);
            x = (double) Math.round(x * 100) / 100;
            y = (double) Math.round(y * 100) / 100;
            Node nd = new Node(i, x, y);
            nodesList.put( nd.getName(), nd);

        }
        // end of createNode
    }

    void createAdjacentList(){

        for (int i = 1; i <= numOfNodes; i++){
            adjList.put(i , new HashSet<>());
        }

        for(int node1: nodesList.keySet()) {
            Node axis1 = nodesList.get(node1);
            for(int node2: nodesList.keySet()) {
                Node axis2 = nodesList.get(node2);

                if(node1 == node2) {
                    continue;
                }
                double xAxis1 = axis1.getxAxis();
                double yAxis1 = axis1.getyAxis();

                double xAxis2 = axis2.getxAxis();
                double yAxis2 = axis2.getyAxis();

                double distance =  Math.sqrt(((xAxis1-xAxis2)*(xAxis1-xAxis2)) + ((yAxis1-yAxis2)*(yAxis1-yAxis2)));

                if(distance <= TL) {
                    HashSet<Integer> tempList = adjList.get(node1);
                    tempList.add(node2);
                    adjList.put(node1, tempList);

                    tempList = adjList.get(node2);
                    tempList.add(node1);
                    adjList.put(node2, tempList);

                    HashSet<Integer> tempDist = new HashSet<>();
                    tempDist.add(node1);
                    tempDist.add(node2);
                    shortPathDistList.put(tempDist, distance);

                    Edge temp = new Edge( node1, node2 , distance);
                    mimSpanDistList.add(temp);
                }
            }
        }
        // end of createAdjacentList
    }

    void queueBSF(int n, Set<Integer> connectedNode){
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        if (explored.containsKey(n)){
            return;
        }
        queue.add(n);

        while (!queue.isEmpty()){
            int x = queue.poll();
            if(!explored.containsKey(x)){
                explored.put(x,true);
                connectedNode.add(x);

                for (int i : adjList.get(x)){
                    if(!explored.containsKey(i)){
                        queue.add(i);
                        connectedNode.add(i);
                    }
                }
            }

        }

    }



    void checkConnectivity (){
        // BSF queue
        System.out.println("\nExecuting queue BSF Algorithm");
        for (int node: adjList.keySet()){
            Set<Integer> connectedNode = new LinkedHashSet<>();
            queueBSF(node,connectedNode);

            if(!connectedNode.isEmpty()){
                connectedNodes.add(connectedNode);
            }
        }

        if(connectedNodes.size() == 1) {
            System.out.println("Graph is fully connected with one connected component.");
            connected = true;
        } else {
            System.out.println("The network is not connected");
            connected = false;
        }

    }

    void printNodes(){
        Random random = new Random();
        System.out.println("\n---List of Data Node---");
        for(int i = 1; i <= numOfDataNodes; i++) {
            int temp = random.nextInt(nodesList.size() - 1 + 1) + 1;
            System.out.println("\tData Node " + temp);
            dataNodes.add(temp);
        }

        System.out.println("\n--List of Storage Node--");
        for (int i = 1; i <= numOfNodes; i++){
            if (dataNodes.contains(i)){

            } else
                System.out.println("\tStorage Node " + i);
        }
    }

    void drawGraph(){
        // Draws Graph
        SensorNetworkGraph graph = new SensorNetworkGraph();
        graph.setGraphWidth(width);
        graph.setGraphHeight(height);
        graph.setNodes(nodesList);
        graph.setAdjList(adjList);
        graph.setPreferredSize(new Dimension(960, 800));
        Thread graphThread = new Thread(graph);
        graphThread.start();
    }

    void printDist(){
        System.out.println();
        for (Object i: shortPathDistList.keySet()){
            System.out.print(i);
            System.out.printf(" with a distance %.2f \n",shortPathDistList.get(i));
        }
    }

    void DijkstraAlgorithm(){
        double distance;
        double[] dist = new double[numOfNodes + 1];
        int[] prev = new int[numOfNodes + 1];
        for(int i = 1; i <= numOfNodes; i++){
            dist[i] = Integer.MAX_VALUE;
            prev[i] = 0;
        }

        dist[DN] = 0;

        PriorityQueue<Edge> queue = new PriorityQueue<>(numOfNodes, new Edge());
        Edge tempDis = new Edge(DN, dist[DN]);

        queue.add(tempDis);

        while (!queue.isEmpty()) {


            int u = queue.poll().getSourc();

            if(u == SN){
                u = SN;
                if( prev[u] != 0){
                    while (u != 0){
                        s.add(u);
                        u = prev[u];
                    }
                }
                if(!s.isEmpty()){
                    break;
                }
            }

            for (int v : adjList.get(u)) {
                temp = new HashSet<>();
                temp.add(u);
                temp.add(v);
                distance = (dist[u] + shortPathDistList.get(temp));
                if (dist[v] > distance) {
                    dist[v] = distance;
                    prev[v] = u;
                    tempDis = new Edge(v, dist[v]);
                    queue.add(tempDis);
                }
            }

        }

        Collections.reverse(s);
        listOfDistance = dist.clone();


    }

    void KruskalAlgorithm(){
        List<Edge> edge = new ArrayList<>();

        Collections.sort(mimSpanDistList , Comparator.comparingDouble(Edge::getDistance));


        DisjointSet ds = new DisjointSet();
        ds.makeSet(numOfNodes + 1);

        int index = 0;
        while (edge.size() != numOfNodes - 1){
            Edge next_edge = mimSpanDistList.get(index++);

            int x = ds.Find(next_edge.getSourc());
            int y = ds.Find(next_edge.getDest());

            if(x != y){
                edge.add(next_edge);
                ds.Union(x,y);
            }

        }

        Collections.sort(edge, Comparator.comparingInt(Edge::getSourc));

        System.out.println("\n----Path Created----\n" + edge.toString());

    }

    void PrimsAlgorithm(){
        double[] dist = new double[numOfNodes + 1];
        boolean explored[] = new boolean[numOfNodes + 1];
        int prev[] = new int[numOfNodes + 1];
        for (int i = 1; i < dist.length; i++){
            dist[i] = Integer.MAX_VALUE;
        }

        dist[DN] = 0;
        PriorityQueue<Edge> queue = new PriorityQueue<>(numOfNodes, new Edge());
        Edge tempDis = new Edge(DN, dist[DN]);
        queue.add(tempDis);

        while (!queue.isEmpty()){

            int u = queue.poll().getSourc();

            if(u == SN){
                u = SN;
                if( prev[u] != 0){
                    while (u != 0){
                        s.add(u);
                        u = prev[u];
                    }
                }
                if(!s.isEmpty()){
                    break;
                }
            }

            if(explored[u] == false){
                explored[u] = true;
            }else if(queue.size()- 1  > 0){
                do {
                    u = queue.poll().getSourc();
                }while (explored[u] == true);
                explored[u] = true;
            }

            for (int v: adjList.get(u)){
                temp = new HashSet<>();
                temp.add(u);
                temp.add(v);
                if(dist[v] > shortPathDistList.get(temp) && explored[v] != true){
                    dist[v] = shortPathDistList.get(temp);
                    prev[v] = u;
                    tempDis = new Edge(v, dist[v]);
                    queue.add(tempDis);
                }
            }
        }

        Collections.reverse(s);
        listOfDistance = dist.clone();
    }


    void calcEnergyCostDJAlgo(){
        double distanceOfIndex = listOfDistance[SN];
        double result;
        result = 2 * (100 * Math.pow(10, -9)) * 1 + (100 * Math.pow(10, -12)) * 1 * Math.pow(distanceOfIndex, 2);
        System.out.println("\nThe engery cost of one data packet is:  " + result + "joules");

        result = 2 * (100 * Math.pow(10, -9)) * DN + (100 * Math.pow(10, -12)) * DN * Math.pow(distanceOfIndex, 2);
        System.out.println("The energy cost of all data packet is " + result + "joules");
    }

    void calcEnergyCostPrmAlgo(){
        double d = 0;
        for (int i = 0; i < s.size(); i++){
            if( i + 1 < s.size()){
                temp.add(s.get(i));
                temp.add(s.get(i+1));
                if(!shortPathDistList.containsKey(temp)) {
                    temp.clear();
                    temp.add(s.get(i+1));
                    temp.add(s.get(i));
                }
                d = shortPathDistList.get(temp);
            }

        }

        double result;
        result = 2 * (100 * Math.pow(10, -9)) * 1 + (100 * Math.pow(10, -12)) * 1 * Math.pow(d, 2);
        System.out.println("\nThe engery cost of one data packet is:  " + result + "joules");

        result = 2 * (100 * Math.pow(10, -9)) * DN + (100 * Math.pow(10, -12)) * DN * Math.pow(d, 2);
        System.out.println("The energy cost of all data packet is " + result + "joules");
    }

    void printAdjlist(){
        System.out.println("\nAdjacency List: ");

        for(int i: adjList.keySet()) {
            System.out.print(i);
            if(!adjList.isEmpty()){
                for(int j: adjList.get(i)) {
                    System.out.print("->" + j);
                }
            }
            System.out.println();
        }
    }

    void drawGraphPath(){
        // Draws Path on Graph
        SensorNetworkGraph graph = new SensorNetworkGraph();
        graph.setGraphWidth(width);
        graph.setGraphHeight(height);
        graph.setNodes(nodesList);
        graph.setAdjList(adjList);
        graph.setDrawPath(true);
        graph.setNodesConnectedPath(s);
        graph.setPreferredSize(new Dimension(960, 800));
        Thread graphThread = new Thread(graph);
        graphThread.start();
    }

    void bellmanFordAlgo(){
        double distance;
        double[] dist = new double[numOfNodes + 1];
        int[] prev = new int[numOfNodes + 1];
        for(int i = 1; i <= numOfNodes; i++){
            dist[i] = Integer.MAX_VALUE;
            prev[i] = 0;
        }

        dist[DN] = 0;

        for (int v = 1; v <= numOfNodes; v++){

            if(v == SN){
                v = SN;
                if( prev[v] != 0){
                    while (v != 0){
                        s.add(v);
                        v = prev[v];
                    }
                }
                if(!s.isEmpty()){
                    break;
                }
            }

            for (int u : adjList.get(v)){
                temp = new HashSet<>();
                temp.add(v);
                temp.add(u);
                distance = (dist[v] + shortPathDistList.get(temp));
                if (dist[u] > distance) {
                    dist[u] = distance;
                    prev[u] = v;
                }
            }
        }

        for(HashSet<Integer> i: shortPathDistList.keySet()){
            List<Integer> temp = new ArrayList<Integer>(i);
            int v = temp.get(0);
            int u = temp.get(1);

            if(dist[u] + shortPathDistList.get(i) < dist[v]){
                System.out.println("Error: Negative Cycle Exits ");
                return;
            }
        }

        Collections.reverse(s);
        listOfDistance = dist.clone();
    }

    double smPathWithKEdge(int prv[]){
        double dist[][][] = new double[numOfNodes + 1][numOfNodes + 1][ k + 2];

        for( int e  = 0; e <= k; e++){

            for (int u: adjList.keySet()){

                for(int v: adjList.get(u)){

                    dist[u][v][e] = Integer.MAX_VALUE;

                    temp = new HashSet<>();
                    temp.add(u);
                    temp.add(v);

                    if(e == 0 && u == v){
                        dist[u][v][e] = 0;
                    }
                    if(e == 1 &&(shortPathDistList.get(temp) != null && shortPathDistList.get(temp) != Integer.MAX_VALUE)){
                        prv[v] = u;
                        dist[u][v][e] = shortPathDistList.get(temp);
                    }

                    if(e > 1){
                        for(int a : adjList.get(u)){

                            temp = new HashSet<>();
                            temp.add(u);
                            temp.add(a);

                            if((shortPathDistList.get(temp) != null && shortPathDistList.get(temp) != Integer.MAX_VALUE) && u != a && v != a && dist[a][v][e-1] != Integer.MAX_VALUE){

                                if(dist[u][v][e] <= (shortPathDistList.get(temp) +dist[a][v][e-1] )){
                                    dist[u][v][e] = dist[u][v][e];
                                }else {
                                    prv[a] = u;
                                    dist[u][v][e] = (shortPathDistList.get(temp) +dist[a][v][e-1] );
                                }
                            }
                        }
                    }
                }
            }
        }
        prv[SN] = 0;
        return dist[DN][SN][k];
    }

    void calcEnergyCostKEdge(double test){
        double result;
        result = 2 * (100 * Math.pow(10, -9)) * 1 + (100 * Math.pow(10, -12)) * 1 * Math.pow(1, 2);
        System.out.println("\nThe engery cost of one data packet is:  " + result + "joules");

        result = 2 * (100 * Math.pow(10, -9)) * DN + (100 * Math.pow(10, -12)) * DN * Math.pow(test, 2);
        System.out.println("The energy cost of all data packet is " + result + "joules");
    }

    void populateS(int prv[]){
        int u = DN;
        if(prv[u] != 0){
            while (u != 0){
                s.add(u);
                u = prv[u];
            }
        }
        Collections.reverse(s);
    }

    //End of Program
}
