package mapvis.layouts.epea;

import mapvis.layouts.pea.model.*;

public class Estimator  {

    MapModel mapModel;

    public double weightOfPressure  = 1;
    public double weightOfConvexity = 3;


    public Estimator(MapModel mapModel){
        this.mapModel = mapModel;
    }

    public Estimator(MapModel mapModel, double weightOfPressure, double weightOfConvexity) {
        this.mapModel = mapModel;
        this.weightOfPressure = weightOfPressure;
        this.weightOfConvexity = weightOfConvexity;
    }

    public int estimate(Vertex vertex) {
        double convexity = calcConvexity(vertex);
        double pressure  = calcPressure(vertex);

        return (int)( - pressure * weightOfPressure + convexity * weightOfConvexity);
    }

    private double calcPressure(Vertex vertex) {
        Polygon polygon = vertex.polygon;
        double pressure = polygon.mass - polygon.area + vertex.momentum;
        //if (vertex.momentum >= 0)
        //    return pressure + 10;
        return pressure;
    }

    public double calcConvexity(Vertex vertex){
        Polygon polygon = vertex.polygon;
        int index = vertex.indexOfVertex;

        Vertex prev = polygon.getVertex(index - 1 < 0 ? (polygon.npoints - 1) : (index - 1));
        Vertex next = polygon.getVertex(index + 1 > polygon.npoints - 1 ? 0 : (index + 1));

        double convexity = (vertex.moveCount - prev.moveCount) + (vertex.moveCount - next.moveCount);

        return convexity;
    }

}
