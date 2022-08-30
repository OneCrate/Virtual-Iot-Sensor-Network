import java.util.AbstractMap;
import java.util.HashMap;

public class Node  {
    double xAxis, yAxis;
    int name;

    public Node(){
        xAxis = 0;
        yAxis = 0;
    }

    public Node(int nm, double x, double y){
        this.name = nm;
        this.xAxis = x;
        this.yAxis = y;
    }

     public Node(double x , double y){
        this.xAxis = x;
        this.yAxis = y;
    }

    public void setxAxis(double x) {
        this.xAxis = x;
    }

    public void setyAxis(double y) {
        this.yAxis = y;
    }

    public void setName(int name) {
        this.name = name;
    }

    public double getxAxis() {
        return xAxis;
    }

    public double getyAxis() {
        return yAxis;
    }

    public int getName() {
        return name;
    }
}
