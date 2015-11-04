package mapvis.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/26/2015.
 */
public class Region<T> {
    final T nodeItem;
    private List<Region<T>> childRegions;
    private List<Tile<T>> borderElements;

    public Region(List<Region<T>> enclosingElements, T nodeItem) {
        this.childRegions = enclosingElements;
        this.borderElements = new ArrayList<>();
        this.nodeItem = nodeItem;
    }

    private void computeBorder(){

    }

    public List<Tile<T>> findBorders(){
        computeBorder();
        return borderElements;
    }

    public boolean isLeaf(){
        return false;
    }

    public List<Region<T>> getChildRegions() {
        return childRegions;
    }
}