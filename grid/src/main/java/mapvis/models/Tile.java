package mapvis.models;

public class Tile<T> {
    int x;
    int y;
    T obj;
    int tag;

    Pos pos;

    final static int EMPTY = -1;
    final static int LAND = 0;
    final static int SEA = 1;  // territorial sea

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public T getObj() {
        return obj;
    }

    public Pos getPos(){ return pos; }

    public boolean isEmpty(){return tag == EMPTY;}

    public Tile(int x, int y){
        this(x,y,null, EMPTY);
    }

    public Tile(int x, int y, T obj) {
        this(x, y, obj, LAND);
    }

    public Tile(int x, int y, T obj, int tag){
        this.x = x;
        this.y = y;
        this.pos = new Pos(x,y);
        this.obj = obj;
        this.tag = tag;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        //if (obj != null ? !obj.equals(tile.obj) : tile.obj != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        //result = 31 * result + (obj != null ? obj.hashCode() : 0);
        return result;
    }

}
