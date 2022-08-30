import java.util.Comparator;

public class Edge implements CompareEdge<Edge> {
    private int sourc, dest;
    private double distance;

    public Edge(){
        this.sourc = 0;
        this.dest = 0;
        this.distance = 0;
    }

    public Edge(int nd1,double dist){
        this.sourc = nd1;
        this.dest = 0;
        this.distance = dist;
    }

    public Edge(int nd1, int nd2, double dist){
        this.sourc = nd1;
        this.dest = nd2;
        this.distance = dist;
    }

    public Edge(Edge e){
        this.sourc = e.getSourc();
        this.dest = e.getDest();
        this.distance = e.getDistance();
    }

    public double getDistance() {
        return distance;
    }

    public int getSourc(){
        return sourc;
    }

    public int getDest() {
        return dest;
    }



    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDest(int dest) {
        this.dest = dest;
    }

    public void setSourc(int sourc) {
        this.sourc = sourc;
    }

    @Override
    public int compare(Edge o1, Edge o2) {
        if(o1.getDistance() < o2.getDistance()){
            return -1;
        }else if(o1.getDistance() > o2.getDistance()){
            return 1;
        }
        return 0;
    }

    @Override
    public int compareSrc(Edge o1, Edge o2) {
        if (o1.getSourc() == o2.getSourc()){
            return 1;
        }
        return 0;
    }

    @Override
    public int compareConnections(Edge o1, Edge o2) {
        return 0;
    }

    @Override
    public String toString(){
        String result = "(" + sourc + ", " + dest + ", " + distance + ")";
        return result;
    }

}
