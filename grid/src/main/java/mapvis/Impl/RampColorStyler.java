package mapvis.Impl;

import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.models.Grid;

import java.util.HashMap;
import java.util.Map;

public class RampColorStyler<T> extends TileStylerBase<T> {
    public int level;
    public int depth;
    Map<T, Color> map = new HashMap<>();
    private Color background;

    public RampColorStyler(Tree2<T> tree, Grid<T> grid, int level, Color background) {
        super(tree, grid);
        this.background = background;
        this.level = level;

        rec(tree.getRoot(), null);
    }

    void rec(T leaf, Color color){
        if (leaf == null)
            return;
        if (tree.getDepth(leaf) <= level) {

            double upper = (6-level) * 100;
            double lower = 0;
            double x = tree.getWeight(leaf);
            x = Math.max(lower, x);
            x = Math.min(x, upper);

            x = (x - lower) / (upper - lower);

            color = Color.color(x, 0.0, 0.0);
        }

        map.put(leaf, color);

        for (T child : tree.getChildren(leaf)) {
            rec(child, color);
        }
        depth = Math.max(tree.getDepth(leaf), depth);
    }





    @Override
    protected Color getColorByValue(T v) {
        return map.get(v);
    }

    @Override
    protected double getBorderWidthByLevel(int l) {
        return (depth + 1 - l)*(depth + 1 - l)/2.0;
    }

    @Override
    public Color getBackground() {
        return background;
    }

}
